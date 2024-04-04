package com.example.bondoman

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bondoman.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors


class ScanFragment : Fragment() {
    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var cameraManager: CameraManager
    lateinit var capReq: CaptureRequest.Builder
    lateinit var textureView: TextureView
    lateinit var cameraCaptureSession: CameraCaptureSession
    lateinit var imageReader: ImageReader

    private lateinit var captureButton: ImageButton
    private lateinit var recaptureButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var confirmButton: ImageButton


    private lateinit var imageFile: File

    private var job: Job? = null
    private var cameraDevice: CameraDevice? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)
        captureButton = view.findViewById(R.id.captureButton)
        recaptureButton = view.findViewById(R.id.recaptureButton)
        galleryButton = view.findViewById(R.id.galleryButton)
        confirmButton = view.findViewById(R.id.confirmButton)

        val navbar = requireActivity().findViewById<LinearLayout>(R.id.navbar_main)
        val toolbar = requireActivity().findViewById<RelativeLayout>(R.id.toolbar)
        val textView = toolbar.findViewById<TextView>(R.id.toolbar_text)
        val transactionButton = requireActivity().findViewById<ImageButton>(R.id.transaction_button)
        val graphButton = requireActivity().findViewById<ImageButton>(R.id.graph_button)
        val settingButton = requireActivity().findViewById<ImageButton>(R.id.setting_button)
        toolbar.setBackgroundColor(Color.parseColor("#1B1A55"))
        navbar.setBackgroundResource(R.drawable.navbar_bordered_background)

        textView.text = "Scan Nota"
        transactionButton.isSelected = false
        graphButton.isSelected = false
        settingButton.isSelected = false

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(CAMERA_PERMISSION)

    }

    override fun onDestroy() {
        val navbar = requireActivity().findViewById<LinearLayout>(R.id.navbar_main)
        val toolbar = requireActivity().findViewById<RelativeLayout>(R.id.toolbar)

        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        navbar.setBackgroundResource(R.drawable.navbar_background)
        super.onDestroy()
        cameraDevice?.close()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
        textureView.surfaceTexture?.release()
        textureView.surfaceTextureListener = null

    }

    private fun configureCameraPreview() {
        capReq = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surface = Surface(textureView.surfaceTexture)
        capReq.addTarget(surface)
        val outputConfigurationSurface = OutputConfiguration(surface)
        val outputConfigurationImageReader = OutputConfiguration(imageReader.surface)
        val sessionConfiguration = SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            listOf(outputConfigurationSurface, outputConfigurationImageReader),
            Executors.newSingleThreadExecutor(),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {

                }
            }
        )

        cameraDevice!!.createCaptureSession(sessionConfiguration)

    }

    private fun displayCapturedImage() {
        cameraCaptureSession.stopRepeating()

        val image = imageReader.acquireLatestImage()
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        val file = File(requireActivity().getExternalFilesDir(null), "selectedImage.jpg")
        file.writeBytes(bytes)
        imageFile = file

        requireActivity().runOnUiThread {
            textureView.surfaceTexture?.let { _ ->
                val canvas = textureView.lockCanvas()
                if (canvas != null) {
                    canvas.drawBitmap(bitmapImage, 0f, 0f, null)
                    textureView.unlockCanvasAndPost(canvas)
                }
            }
        }
        image.close()
    }

    private fun displayUploadedImage(uri: Uri) {
        captureButton.visibility = View.INVISIBLE
        galleryButton.visibility = View.INVISIBLE
        recaptureButton.visibility = View.VISIBLE
        confirmButton.visibility = View.VISIBLE
        val imageView = view?.findViewById<ImageView>(R.id.imageView)
        if (imageView != null) {
            imageView.setImageURI(uri)
            imageView.visibility = View.VISIBLE
            textureView.visibility = View.GONE
        }

        var parcelFileDescriptor: ParcelFileDescriptor? = null
        try {
            parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val file = File(requireActivity().cacheDir, "MyImage.jpg")
            val inputStream = FileInputStream(fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            imageFile = file
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        } finally {
            try {
                parcelFileDescriptor?.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, e.message.toString())
            }
        }

    }

    private fun createPopUp(title: String, message: String) {
        if (isAdded) {
            val dialogBuilder = AlertDialog.Builder(requireActivity())
            dialogBuilder.setTitle(title)
            dialogBuilder.setMessage(message)

            dialogBuilder.setPositiveButton("Ok") { _, _ ->
                findNavController().navigate(R.id.transaction_fragment)
            }
            requireActivity().runOnUiThread {
                dialogBuilder.show()
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                displayUploadedImage(uri)
            }
        }

    private fun openCamera() {

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), CAMERA_PERMISSION, 0
            )
            return
        }
        cameraManager.openCamera(
            cameraManager.cameraIdList[0],
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    capReq = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    val surface = Surface(textureView.surfaceTexture)
                    capReq.addTarget(surface)
                    val outputConfigurationSurface = OutputConfiguration(surface)
                    val outputConfigurationImageReader = OutputConfiguration(imageReader.surface)
                    val sessionConfiguration = SessionConfiguration(
                        SessionConfiguration.SESSION_REGULAR,
                        listOf(outputConfigurationSurface, outputConfigurationImageReader),
                        Executors.newSingleThreadExecutor(),
                        object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                cameraCaptureSession = session
                                cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {

                            }
                        }
                    )

                    cameraDevice!!.createCaptureSession(sessionConfiguration)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    cameraDevice?.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    cameraDevice?.close()
                    Log.e(ContentValues.TAG, "Camera error: $error")
                }

            },
            handler
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    textureView = view?.findViewById(R.id.textureView)!!
                    cameraManager =
                        requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    handlerThread = HandlerThread("videoThread")
                    handlerThread.start()
                    handler = Handler(handlerThread.looper)

                    textureView.surfaceTextureListener =
                        object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                                openCamera()
                            }

                            override fun onSurfaceTextureSizeChanged(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                            }

                            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {

                                return false
                            }

                            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                            }

                        }

                    imageReader = ImageReader.newInstance(1080, 1323, ImageFormat.JPEG, 1)

                    captureButton.setOnClickListener {
                        captureButton.visibility = View.INVISIBLE
                        galleryButton.visibility = View.INVISIBLE
                        recaptureButton.visibility = View.VISIBLE
                        confirmButton.visibility = View.VISIBLE
                        capReq =
                            cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                        capReq.addTarget(imageReader.surface)
                        cameraCaptureSession.capture(
                            capReq.build(),
                            object : CameraCaptureSession.CaptureCallback() {
                                override fun onCaptureCompleted(
                                    session: CameraCaptureSession,
                                    request: CaptureRequest,
                                    result: TotalCaptureResult
                                ) {
                                    super.onCaptureCompleted(session, request, result)
                                    displayCapturedImage()
                                }
                            },
                            null
                        )
                    }


                    recaptureButton.setOnClickListener {
                        captureButton.visibility = View.VISIBLE
                        galleryButton.visibility = View.VISIBLE
                        recaptureButton.visibility = View.INVISIBLE
                        confirmButton.visibility = View.INVISIBLE
                        textureView.visibility = View.VISIBLE
                        requireView().findViewById<ImageView>(R.id.imageView).visibility = View.GONE
                        configureCameraPreview()
                    }


                    galleryButton.setOnClickListener {
                        getContent.launch("image/*")
                    }

                    confirmButton.setOnClickListener {
                        job = CoroutineScope(Dispatchers.IO).launch {
                            val sharedPreferences =
                                requireActivity().getSharedPreferences(
                                    "sharedPrefs",
                                    Context.MODE_PRIVATE
                                )
                            val token = sharedPreferences.getString("TOKEN", "") ?: ""
                            try {
                                val requestFile = RequestBody.create(
                                    MediaType.parse("image/jpg"),
                                    imageFile
                                )
                                val body = MultipartBody.Part.createFormData(
                                    "file",
                                    imageFile.name,
                                    requestFile
                                )
                                val response =
                                    RetrofitInstance.api.uploadBill("Bearer $token", body)
                                println("Bearer $token")
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    println(responseBody)
                                    createPopUp(
                                        "Berhasil",
                                        "Transaksi berhasil ditambahkan"
                                    )
                                } else {
                                    createPopUp(
                                        "Gagal",
                                        "Nota gagal dibaca. Error: ${
                                            response.errorBody()?.toString()
                                        }"
                                    )
                                }
                            } catch (e: HttpException) {
                                Log.e(ContentValues.TAG, e.message())
                            } catch (e: Throwable) {
                                Log.e(ContentValues.TAG, e.stackTraceToString())
                            }

                        }
                    }

                } else {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    companion object {
        private val CAMERA_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}