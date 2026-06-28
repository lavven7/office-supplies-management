package com.lavven777.officesupplies.domain.itemrequest.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    @Transactional
    public void approveRequest(Long requestId, Long approverId) {

    }

}
