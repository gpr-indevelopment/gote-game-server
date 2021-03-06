package com.gpr.gotegameserver;

import com.gpr.gotegameserver.signaling.SignalingWebSocketClient;
import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.elements.WebRTCBin;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class MediaPipelineFactory {

    private static final String VIDEO_BIN_DESCRIPTION = "dxgiscreencapsrc cursor=true ! capsfilter caps=\"video/x-raw,framerate=60/1\" ! queue ! nvh264enc bitrate=2250 rc-mode=cbr gop-size=-1 qos=true preset=low-latency-hq ! capsfilter caps=\"video/x-h264,profile=high\" ! queue ! rtph264pay ! capsfilter caps=\"application/x-rtp,media=video,encoding-name=H264,width=1280,height=720,payload=123\"";

    //private static final String AUDIO_BIN_DESCRIPTION = "audiotestsrc ! audioconvert ! audioresample ! queue ! opusenc ! rtpopuspay ! queue ! capsfilter caps=application/x-rtp,media=audio,encoding-name=OPUS,payload=96";

    private final SignalingWebSocketClient signalingWebSocketClient;

    public MediaPipelineFactory(SignalingWebSocketClient signalingWebSocketClient) {
        this.signalingWebSocketClient = signalingWebSocketClient;
    }

    @PreDestroy
    public void destroyGst() {
        Gst.quit();
    }

    public void buildPipeline(String peerId) {
        Gst.init();
        WebRTCBin webRTCBin = new WebRTCBin("gstreamer-webrtc-server");
        Bin video = Gst.parseBinFromDescription(VIDEO_BIN_DESCRIPTION, true);
        Pipeline pipeline = new Pipeline();
        pipeline.addMany(webRTCBin, video);
        video.link(webRTCBin);
        setMediaDirection(webRTCBin);
        signalingWebSocketClient.startWebSocketConnection(peerId, pipeline, webRTCBin);
    }

    // Forcing transmission to be unidirectional (send only)
    private void setMediaDirection(WebRTCBin webRTCBin) {
        GstObject videoTransceiver = webRTCBin.emit(GstObject.class, "get-transceiver", 0);
        videoTransceiver.set("direction", 2);
//        GstObject audioTransceiver = webRTCBin.emit(GstObject.class, "get-transceiver", 1);
//        audioTransceiver.set("direction", 2);
    }
}
