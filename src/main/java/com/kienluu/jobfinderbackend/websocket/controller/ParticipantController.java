package com.kienluu.jobfinderbackend.websocket.controller;

import com.kienluu.jobfinderbackend.websocket.model.Participant;
import com.kienluu.jobfinderbackend.websocket.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message/participant")
@AllArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;


    @GetMapping("/{id}")
    public ResponseEntity<Object> getParticipants(@PathVariable String id) {
        try{
            Participant participant = participantService.findParticipantById(id);
            return new ResponseEntity<>(participant, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
