package com.example.sparrow.configservice.controller.client;

import com.example.sparrow.configservice.entity.Namespace;
import com.example.sparrow.configservice.entity.Release;
import com.example.sparrow.configservice.event.ReleaseEvent;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.NamespaceRepository;
import com.example.sparrow.configservice.repository.ReleaseRepository;
import com.example.sparrow.configservice.util.EntityManagerUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/client/notification")
public class NotificationController {
    private static final long LONG_POLL_TIMEOUT_IN_MILLISECOND = 60 * 1000L;
    private final Multimap<Long,DeferredResult<ResponseEntity<Long>>> waiting =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());

    @Autowired
    private NamespaceRepository namespaceRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private EntityManagerUtils entityManagerUtils;

    @GetMapping("/namespaceName/{namespaceName}/notificationId/{notificationId}")
    public DeferredResult<ResponseEntity<Long>> notification(
            @PathVariable String namespaceName,
            @PathVariable Long notificationId
    ) {
        Namespace namespace = namespaceRepository.findByName(namespaceName).orElseThrow(() -> new ResourceNotFoundException("Namespace not found"));
        Long namespaceId = namespace.getId();
        DeferredResult<ResponseEntity<Long>> deferredResult = new DeferredResult<>(LONG_POLL_TIMEOUT_IN_MILLISECOND, new ResponseEntity<>(HttpStatus.NOT_MODIFIED));
        deferredResult.onCompletion(() -> waiting.remove(namespaceId, deferredResult));
        waiting.put(namespaceId, deferredResult);
        Optional<Release> releaseOptional = releaseRepository.findFirstByNamespaceIdOrderByIdDesc(namespaceId);
        entityManagerUtils.closeEntityManager();
        releaseOptional.ifPresent(release -> {
            if (release.getId() > notificationId) {
                log.info("Will return new release id: {}", release.getId());
                deferredResult.setResult(new ResponseEntity<>(release.getId(), HttpStatus.OK));
            }
        });
        return deferredResult;
    }

    @EventListener(ReleaseEvent.class)
    public void handleRelease(ReleaseEvent releaseEvent) {
        log.info("Handle release event: {}", releaseEvent);
        List<DeferredResult<ResponseEntity<Long>>> results = new ArrayList<>(waiting.get(releaseEvent.getNamespaceId()));
        for (DeferredResult<ResponseEntity<Long>> deferredResult : results) {
            deferredResult.setResult(new ResponseEntity<>(releaseEvent.getReleaseId(), HttpStatus.OK));
        }
    }
}
