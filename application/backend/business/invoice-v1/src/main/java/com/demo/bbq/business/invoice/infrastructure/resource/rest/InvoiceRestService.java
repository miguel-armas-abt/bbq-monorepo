package com.demo.bbq.business.invoice.infrastructure.resource.rest;

import com.demo.bbq.business.invoice.domain.model.request.PaymentRequest;
import com.demo.bbq.business.invoice.domain.model.request.ProductRequest;
import com.demo.bbq.business.invoice.domain.model.response.ProformaInvoice;
import com.demo.bbq.business.invoice.infrastructure.documentation.data.InvoiceDocumentationMetadata;
import com.demo.bbq.support.exception.documentation.ApiExceptionJsonExample;
import com.demo.bbq.support.exception.model.dto.ApiExceptionDto;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@ApiResponses(value = {
    @ApiResponse(responseCode = "400",
        content = @Content(schema = @Schema(implementation = ApiExceptionDto.class), examples = @ExampleObject(value = ApiExceptionJsonExample.BAD_REQUEST))),
    @ApiResponse(responseCode = "404",
        content = @Content(schema = @Schema(implementation = ApiExceptionDto.class), examples = @ExampleObject(value = ApiExceptionJsonExample.NOT_FOUND))),
})
public interface InvoiceRestService {

  @Operation(summary = "Generate a proforma invoice based on the orders requested on a table", tags = InvoiceDocumentationMetadata.TAG)
  @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ProformaInvoice.class))})
  Single<ProformaInvoice> generateProforma(HttpServletRequest servletRequest,
                                           List<ProductRequest> productList);

  @Operation(summary = "Send proforma invoice to pay and clean the table order", tags = InvoiceDocumentationMetadata.TAG)
  @ApiResponse(responseCode = "201")
  Completable sendToPay(HttpServletRequest servletRequest,
                        HttpServletResponse servletResponse, PaymentRequest paymentRequest);
}