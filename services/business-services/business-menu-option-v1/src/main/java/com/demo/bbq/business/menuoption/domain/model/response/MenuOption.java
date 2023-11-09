package com.demo.bbq.business.menuoption.domain.model.response;

import com.demo.bbq.business.menuoption.infrastructure.documentation.data.MenuOptionExample;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuOption implements Serializable {

  @Schema(example = MenuOptionExample.ID)
  private Long id;

  @Schema(example = MenuOptionExample.DESCRIPTION)
  private String description;

  @Schema(example = MenuOptionExample.CATEGORY_MAIN_DISH)
  private String category;

  @Schema(example = MenuOptionExample.PRICE)
  private BigDecimal price;

  @Schema(example = MenuOptionExample.ACTIVE)
  private boolean active;

}