package com.demo.bbq.rest;

import com.demo.bbq.application.dto.request.MenuOptionSaveRequestDTO;
import com.demo.bbq.application.dto.request.MenuOptionUpdateRequestDTO;
import com.demo.bbq.application.dto.response.MenuOptionResponseDTO;
import com.demo.bbq.config.doc.DocumentationConfig.DocumentationExample;
import com.demo.bbq.config.doc.DocumentationConfig;
import com.demo.bbq.commons.errors.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@ApiResponses(value = {
    @ApiResponse(responseCode = "400",
        content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
})
public interface MenuOptionRestService {

  @Operation(summary = "Get a menu by ID", tags = DocumentationConfig.TAG)
  @ApiResponse(responseCode = "200")
  ResponseEntity<MenuOptionResponseDTO> findByProductCode(HttpServletRequest servletRequest,
                                                          @Parameter(example = DocumentationExample.PRODUCT_CODE) String productCode);

  @Operation(summary = "Get a menu list by category", tags = DocumentationConfig.TAG)
  @ApiResponse(responseCode = "200")
  ResponseEntity<List<MenuOptionResponseDTO>> findByCategory(HttpServletRequest servletRequest,
                                                             @Parameter(description = "Menu category", examples = {
                                                      @ExampleObject(value = DocumentationExample.CATEGORY_MAIN_DISH),
                                                      @ExampleObject(value = DocumentationExample.CATEGORY_DRINK),
                                                      @ExampleObject(value = DocumentationExample.CATEGORY_DESSERT),
                                                  }) String categoryCode);

  @Operation(summary = "Save a menu", tags = DocumentationConfig.TAG)
  @ApiResponse(responseCode = "201")
  ResponseEntity<Void> save(HttpServletRequest servletRequest,
                            @RequestBody(content = {@Content(schema = @Schema(implementation = MenuOptionSaveRequestDTO.class))}) MenuOptionSaveRequestDTO menuOption);

  @Operation(summary = "Update a menu", tags = DocumentationConfig.TAG)
  @ApiResponse(responseCode = "201")
  ResponseEntity<Void> update(HttpServletRequest servletRequest,
                              @RequestBody(content = {@Content(schema = @Schema(implementation = MenuOptionUpdateRequestDTO.class))}) MenuOptionUpdateRequestDTO menuOption,
                              @Parameter(example = DocumentationExample.PRODUCT_CODE) String productCode);

  @Operation(summary = "Delete a menu", tags = DocumentationConfig.TAG)
  @ApiResponse(responseCode = "204")
  ResponseEntity<Void> delete(HttpServletRequest servletRequest,
                              @Parameter(example = DocumentationExample.PRODUCT_CODE) String productCode);
}
