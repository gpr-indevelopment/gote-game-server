package com.gpr.edgegameserver.gstreamerserver;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stream")
@CrossOrigin("*")
public class StreamerRestController {

    private final MediaPipelineFactory mediaPipelineFactory;

    private final InputLagService inputLagService;

    public StreamerRestController(MediaPipelineFactory mediaPipelineFactory, InputLagService inputLagService) {
        this.mediaPipelineFactory = mediaPipelineFactory;
        this.inputLagService = inputLagService;
    }

    @PostMapping("/{peerId}")
    public void startStream(@PathVariable String peerId) {
        mediaPipelineFactory.buildPipeline(peerId);
    }

    @PostMapping("/input-lag")
    public void registerInputLag(@RequestBody Map<String, Object> payload) {
        inputLagService.register((Long) payload.get("sentTimestamp"));
    }
}