package com.lavven777.officesupplies.domain.itemrequest.repository;

import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequester(User requester);
}