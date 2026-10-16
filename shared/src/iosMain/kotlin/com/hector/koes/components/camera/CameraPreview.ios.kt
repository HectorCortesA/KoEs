@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.hector.koes.components.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFoundation.*
import platform.Foundation.NSError
import platform.Foundation.NSData
import platform.darwin.NSObject
import platform.UIKit.UIView
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

// Conservamos una referencia fuerte al delegado mientras se realiza la captura.
private class PhotoDelegate(
    private val onCaptured: (ByteArray) -> Unit
) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?
    ) {
        if (error != null) return
        val data: NSData = didFinishProcessingPhoto.fileDataRepresentation() ?: return
        val count = data.length.toInt()
        if (count <= 0) return
        val bytes = ByteArray(count)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
        }
        dispatch_async(dispatch_get_main_queue()) { onCaptured(bytes) }
    }
}

private class IOSCamera(
    private val onCaptured: (ByteArray) -> Unit
) {
    val session = AVCaptureSession()
    val photoOutput = AVCapturePhotoOutput()
    val previewLayer = AVCaptureVideoPreviewLayer(session = session).apply {
        videoGravity = AVLayerVideoGravityResizeAspectFill
    }
    private var delegate: PhotoDelegate? = null

    fun configure(front: Boolean) {
        session.beginConfiguration()
        session.inputs.toList().forEach { session.removeInput(it as AVCaptureInput) }
        val position = if (front) AVCaptureDevicePositionFront else AVCaptureDevicePositionBack
        val device = AVCaptureDevice.devicesWithMediaType(AVMediaTypeVideo)
            .filterIsInstance<AVCaptureDevice>()
            .firstOrNull { it.position == position }
        if (device != null) {
            val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null) as? AVCaptureDeviceInput
            if (input != null && session.canAddInput(input)) session.addInput(input)
        }
        if (session.canAddOutput(photoOutput) && session.outputs.none { it == photoOutput }) {
            session.addOutput(photoOutput)
        }
        session.commitConfiguration()
        if (!session.running) session.startRunning()
    }

    fun capture() {
        val handler = PhotoDelegate(onCaptured)
        delegate = handler
        val settings = AVCapturePhotoSettings.photoSettings()
        photoOutput.capturePhotoWithSettings(settings, delegate = handler)
    }

    fun stop() { if (session.running) session.stopRunning() }
}

private class CameraHostView(camera: IOSCamera) : UIView(frame = platform.CoreGraphics.CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    private val preview = camera.previewLayer
    init { this.layer.addSublayer(preview) }
    override fun layoutSubviews() {
        super.layoutSubviews()
        preview.frame = bounds
    }
}

@Composable
actual fun CameraPreview(
    modifier: Modifier,
    isFrontCamera: Boolean,
    captureTrigger: Long,
    onPhotoCaptured: (ByteArray) -> Unit
) {
    val camera = remember { IOSCamera(onPhotoCaptured) }
    UIKitView(
        factory = { CameraHostView(camera) },
        modifier = modifier
    )
    LaunchedEffect(isFrontCamera) {
        when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> camera.configure(isFrontCamera)
            AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    if (granted) dispatch_async(dispatch_get_main_queue()) {
                        camera.configure(isFrontCamera)
                    }
                }
            }
            else -> Unit // El usuario puede habilitar la cámara desde Ajustes de iOS.
        }
    }
    LaunchedEffect(captureTrigger) { if (captureTrigger > 0L) camera.capture() }
    DisposableEffect(Unit) { onDispose { camera.stop() } }
}
