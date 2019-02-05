/*
 * finAPI RESTful Services
 * finAPI RESTful Services
 *
 * OpenAPI spec version: v1.64.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.IbanRuleParams;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Params for creation IBAN rules
 */
@ApiModel(description = "Params for creation IBAN rules")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-05T12:19:21.458Z")
public class IbanRulesParams {
  @SerializedName("ibanRules")
  private List<IbanRuleParams> ibanRules = new ArrayList<IbanRuleParams>();

  public IbanRulesParams ibanRules(List<IbanRuleParams> ibanRules) {
    this.ibanRules = ibanRules;
    return this;
  }

  public IbanRulesParams addIbanRulesItem(IbanRuleParams ibanRulesItem) {
    this.ibanRules.add(ibanRulesItem);
    return this;
  }

   /**
   * IBAN rule definitions. The minimum number of rule definitions is 1. The maximum number of rule definitions is 100.
   * @return ibanRules
  **/
  @ApiModelProperty(required = true, value = "IBAN rule definitions. The minimum number of rule definitions is 1. The maximum number of rule definitions is 100.")
  public List<IbanRuleParams> getIbanRules() {
    return ibanRules;
  }

  public void setIbanRules(List<IbanRuleParams> ibanRules) {
    this.ibanRules = ibanRules;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IbanRulesParams ibanRulesParams = (IbanRulesParams) o;
    return Objects.equals(this.ibanRules, ibanRulesParams.ibanRules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ibanRules);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IbanRulesParams {\n");
    
    sb.append("    ibanRules: ").append(toIndentedString(ibanRules)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
