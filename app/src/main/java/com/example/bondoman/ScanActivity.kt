package com.example.bondoman

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ScanActivity : ComponentActivity() {
    private var isLivePreview = true
    lateinit var capReq: CaptureRequest.Builder
    lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var cameraCaptureSession: CameraCaptureSession
    lateinit var cameraDevice: CameraDevice
    //    lateinit var captureRequest: CaptureRequest
    lateinit var imageReader: ImageReader

    private lateinit var captureButton: ImageButton
    private lateinit var recaptureButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var confirmButton: ImageButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        captureButton = findViewById(R.id.captureButton)
        recaptureButton = findViewById(R.id.recaptureButton)
        galleryButton = findViewById(R.id.galleryButton)
        confirmButton = findViewById(R.id.confirmButton)

        if (!hasRequiredPermissions()) {
            requestPermissionLauncher.launch(CAMERA_PERMISSION)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }

    private fun configureCameraPreview() {
        // Create a capture request for preview


        // Create a new camera capture session for the preview
        capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surface = Surface(textureView.surfaceTexture)
        capReq.addTarget(surface)
        cameraDevice.createCaptureSession(listOf(surface, imageReader.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {

                }

            }, handler)
        isLivePreview = true

    }

    private fun displayCapturedImage() {
        cameraCaptureSession.stopRepeating()

        // Retrieve the captured image data from your ImageReader
        val image = imageReader.acquireLatestImage()
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Display the captured image on your TextureView
        runOnUiThread {
            textureView.surfaceTexture?.let { _ ->
                val canvas = textureView.lockCanvas()
                if (canvas != null) {
                    canvas.drawBitmap(bitmapImage, 0f, 0f, null)
                    textureView.unlockCanvasAndPost(canvas)
                } else {
                    Log.e(TAG, "canvas error")
                }
            }
        }

        // Close and release the Image when done
        image.close()
    }

    private fun openCamera(){

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, CAMERA_PERMISSION, 0
            )
            return
        }
        cameraManager.openCamera(cameraManager.cameraIdList[0], object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                val surface = Surface(textureView.surfaceTexture)
                capReq.addTarget(surface)
                cameraDevice.createCaptureSession(listOf(surface, imageReader.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            cameraCaptureSession = session
                            cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {

                        }

                    }, handler)
            }

            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                cameraDevice.close()
                Log.e(TAG, "Camera error: $error")
            }

        }, handler)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    textureView = findViewById(R.id.textureView)
                    cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    handlerThread = HandlerThread("videoThread")
                    handlerThread.start()
                    handler = Handler(handlerThread.looper)

                    textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
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

                    captureButton.apply {
                        setOnClickListener {
                            captureButton.visibility = View.INVISIBLE
                            galleryButton.visibility = View.INVISIBLE
                            recaptureButton.visibility = View.VISIBLE
                            confirmButton.visibility = View.VISIBLE
                            capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                            capReq.addTarget(imageReader.surface)
                            cameraCaptureSession.capture(capReq.build(), object : CameraCaptureSession.CaptureCallback() {
                                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                                    super.onCaptureCompleted(session, request, result)
                                    displayCapturedImage()
                                }
                            }, null)

                        }
                    }

                    recaptureButton.apply {
                        setOnClickListener {
                            captureButton.visibility = View.VISIBLE
                            galleryButton.visibility = View.VISIBLE
                            recaptureButton.visibility = View.INVISIBLE
                            confirmButton.visibility = View.INVISIBLE
                            configureCameraPreview()
                        }
                    }
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    private fun hasRequiredPermissions(): Boolean {
        return CAMERA_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
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
                )
        }
    }
}