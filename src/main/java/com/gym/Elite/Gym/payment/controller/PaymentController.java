package com.gym.Elite.Gym.payment.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.payment.dto.PaymentRequestDTO;
import com.gym.Elite.Gym.payment.dto.PaymentResponseDTO;
import com.gym.Elite.Gym.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<ResponseDto> createPayment(@RequestBody PaymentRequestDTO request) {

        ResponseDto responseDto = paymentService.createPayment(request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-payment/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable UUID paymentId) {

        PaymentResponseDTO responseDTO = paymentService.getPaymentById(paymentId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/get-payments/member/{memberId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByMember(@PathVariable UUID memberId) {

        List<PaymentResponseDTO> responseDTO = paymentService.getPaymentsByMember(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }
}
