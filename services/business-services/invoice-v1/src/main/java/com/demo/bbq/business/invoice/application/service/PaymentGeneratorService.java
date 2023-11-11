package com.demo.bbq.business.invoice.application.service;

import com.demo.bbq.business.invoice.domain.model.request.PaymentRequest;
import io.reactivex.Completable;

public interface PaymentGeneratorService {

  Completable sendToPay(PaymentRequest paymentRequest);
}