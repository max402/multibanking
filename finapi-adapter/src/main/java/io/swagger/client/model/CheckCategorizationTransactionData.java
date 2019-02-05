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
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Transaction data for categorization check
 */
@ApiModel(description = "Transaction data for categorization check")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-05T12:19:21.458Z")
public class CheckCategorizationTransactionData {
  @SerializedName("transactionId")
  private String transactionId = null;

  @SerializedName("accountTypeId")
  private Long accountTypeId = null;

  @SerializedName("amount")
  private BigDecimal amount = null;

  @SerializedName("purpose")
  private String purpose = null;

  @SerializedName("counterpart")
  private String counterpart = null;

  @SerializedName("counterpartIban")
  private String counterpartIban = null;

  @SerializedName("counterpartAccountNumber")
  private String counterpartAccountNumber = null;

  @SerializedName("counterpartBlz")
  private String counterpartBlz = null;

  @SerializedName("counterpartBic")
  private String counterpartBic = null;

  @SerializedName("mcCode")
  private String mcCode = null;

  @SerializedName("typeCodeZka")
  private String typeCodeZka = null;

  public CheckCategorizationTransactionData transactionId(String transactionId) {
    this.transactionId = transactionId;
    return this;
  }

   /**
   * Identifier of transaction. This can be any arbitrary string that will be passed back in the response so that you can map the results to the given transactions. Note that the identifier must be unique within the given list of transactions.
   * @return transactionId
  **/
  @ApiModelProperty(example = "transaction", required = true, value = "Identifier of transaction. This can be any arbitrary string that will be passed back in the response so that you can map the results to the given transactions. Note that the identifier must be unique within the given list of transactions.")
  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public CheckCategorizationTransactionData accountTypeId(Long accountTypeId) {
    this.accountTypeId = accountTypeId;
    return this;
  }

   /**
   * Identifier of account type.&lt;br/&gt;&lt;br/&gt;1 &#x3D; Checking,&lt;br/&gt;2 &#x3D; Savings,&lt;br/&gt;3 &#x3D; CreditCard,&lt;br/&gt;4 &#x3D; Security,&lt;br/&gt;5 &#x3D; Loan,&lt;br/&gt;6 &#x3D; Pocket (DEPRECATED; will not be returned for any account unless this type has explicitly been set via PATCH),&lt;br/&gt;7 &#x3D; Membership,&lt;br/&gt;8 &#x3D; Bausparen&lt;br/&gt;&lt;br/&gt;
   * minimum: 1
   * maximum: 7
   * @return accountTypeId
  **/
  @ApiModelProperty(example = "1", required = true, value = "Identifier of account type.<br/><br/>1 = Checking,<br/>2 = Savings,<br/>3 = CreditCard,<br/>4 = Security,<br/>5 = Loan,<br/>6 = Pocket (DEPRECATED; will not be returned for any account unless this type has explicitly been set via PATCH),<br/>7 = Membership,<br/>8 = Bausparen<br/><br/>")
  public Long getAccountTypeId() {
    return accountTypeId;
  }

  public void setAccountTypeId(Long accountTypeId) {
    this.accountTypeId = accountTypeId;
  }

  public CheckCategorizationTransactionData amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Amount
   * @return amount
  **/
  @ApiModelProperty(example = "-99.99", required = true, value = "Amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public CheckCategorizationTransactionData purpose(String purpose) {
    this.purpose = purpose;
    return this;
  }

   /**
   * Purpose. Any symbols are allowed. Maximum length is 2000. Default value: null.
   * @return purpose
  **/
  @ApiModelProperty(example = "Restaurantbesuch", value = "Purpose. Any symbols are allowed. Maximum length is 2000. Default value: null.")
  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public CheckCategorizationTransactionData counterpart(String counterpart) {
    this.counterpart = counterpart;
    return this;
  }

   /**
   * Counterpart. Any symbols are allowed. Maximum length is 80. Default value: null.
   * @return counterpart
  **/
  @ApiModelProperty(example = "Bar Centrale", value = "Counterpart. Any symbols are allowed. Maximum length is 80. Default value: null.")
  public String getCounterpart() {
    return counterpart;
  }

  public void setCounterpart(String counterpart) {
    this.counterpart = counterpart;
  }

  public CheckCategorizationTransactionData counterpartIban(String counterpartIban) {
    this.counterpartIban = counterpartIban;
    return this;
  }

   /**
   * Counterpart IBAN. Default value: null.
   * @return counterpartIban
  **/
  @ApiModelProperty(example = "DE13700800000061110500", value = "Counterpart IBAN. Default value: null.")
  public String getCounterpartIban() {
    return counterpartIban;
  }

  public void setCounterpartIban(String counterpartIban) {
    this.counterpartIban = counterpartIban;
  }

  public CheckCategorizationTransactionData counterpartAccountNumber(String counterpartAccountNumber) {
    this.counterpartAccountNumber = counterpartAccountNumber;
    return this;
  }

   /**
   * Counterpart account number. Default value: null.
   * @return counterpartAccountNumber
  **/
  @ApiModelProperty(example = "61110500", value = "Counterpart account number. Default value: null.")
  public String getCounterpartAccountNumber() {
    return counterpartAccountNumber;
  }

  public void setCounterpartAccountNumber(String counterpartAccountNumber) {
    this.counterpartAccountNumber = counterpartAccountNumber;
  }

  public CheckCategorizationTransactionData counterpartBlz(String counterpartBlz) {
    this.counterpartBlz = counterpartBlz;
    return this;
  }

   /**
   * Counterpart BLZ. Default value: null.
   * @return counterpartBlz
  **/
  @ApiModelProperty(example = "70080000", value = "Counterpart BLZ. Default value: null.")
  public String getCounterpartBlz() {
    return counterpartBlz;
  }

  public void setCounterpartBlz(String counterpartBlz) {
    this.counterpartBlz = counterpartBlz;
  }

  public CheckCategorizationTransactionData counterpartBic(String counterpartBic) {
    this.counterpartBic = counterpartBic;
    return this;
  }

   /**
   * Counterpart BIC. Default value: null.
   * @return counterpartBic
  **/
  @ApiModelProperty(example = "DRESDEFF700", value = "Counterpart BIC. Default value: null.")
  public String getCounterpartBic() {
    return counterpartBic;
  }

  public void setCounterpartBic(String counterpartBic) {
    this.counterpartBic = counterpartBic;
  }

  public CheckCategorizationTransactionData mcCode(String mcCode) {
    this.mcCode = mcCode;
    return this;
  }

   /**
   * Merchant category code (for credit card transactions only). May only contain up to 4 digits. Default value: null.
   * @return mcCode
  **/
  @ApiModelProperty(example = "5542", value = "Merchant category code (for credit card transactions only). May only contain up to 4 digits. Default value: null.")
  public String getMcCode() {
    return mcCode;
  }

  public void setMcCode(String mcCode) {
    this.mcCode = mcCode;
  }

  public CheckCategorizationTransactionData typeCodeZka(String typeCodeZka) {
    this.typeCodeZka = typeCodeZka;
    return this;
  }

   /**
   * ZKA business transaction code which relates to the transaction&#39;s type (Number from 0 through 999). Default value: null.
   * @return typeCodeZka
  **/
  @ApiModelProperty(example = "999", value = "ZKA business transaction code which relates to the transaction's type (Number from 0 through 999). Default value: null.")
  public String getTypeCodeZka() {
    return typeCodeZka;
  }

  public void setTypeCodeZka(String typeCodeZka) {
    this.typeCodeZka = typeCodeZka;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckCategorizationTransactionData checkCategorizationTransactionData = (CheckCategorizationTransactionData) o;
    return Objects.equals(this.transactionId, checkCategorizationTransactionData.transactionId) &&
        Objects.equals(this.accountTypeId, checkCategorizationTransactionData.accountTypeId) &&
        Objects.equals(this.amount, checkCategorizationTransactionData.amount) &&
        Objects.equals(this.purpose, checkCategorizationTransactionData.purpose) &&
        Objects.equals(this.counterpart, checkCategorizationTransactionData.counterpart) &&
        Objects.equals(this.counterpartIban, checkCategorizationTransactionData.counterpartIban) &&
        Objects.equals(this.counterpartAccountNumber, checkCategorizationTransactionData.counterpartAccountNumber) &&
        Objects.equals(this.counterpartBlz, checkCategorizationTransactionData.counterpartBlz) &&
        Objects.equals(this.counterpartBic, checkCategorizationTransactionData.counterpartBic) &&
        Objects.equals(this.mcCode, checkCategorizationTransactionData.mcCode) &&
        Objects.equals(this.typeCodeZka, checkCategorizationTransactionData.typeCodeZka);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionId, accountTypeId, amount, purpose, counterpart, counterpartIban, counterpartAccountNumber, counterpartBlz, counterpartBic, mcCode, typeCodeZka);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckCategorizationTransactionData {\n");
    
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    accountTypeId: ").append(toIndentedString(accountTypeId)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    purpose: ").append(toIndentedString(purpose)).append("\n");
    sb.append("    counterpart: ").append(toIndentedString(counterpart)).append("\n");
    sb.append("    counterpartIban: ").append(toIndentedString(counterpartIban)).append("\n");
    sb.append("    counterpartAccountNumber: ").append(toIndentedString(counterpartAccountNumber)).append("\n");
    sb.append("    counterpartBlz: ").append(toIndentedString(counterpartBlz)).append("\n");
    sb.append("    counterpartBic: ").append(toIndentedString(counterpartBic)).append("\n");
    sb.append("    mcCode: ").append(toIndentedString(mcCode)).append("\n");
    sb.append("    typeCodeZka: ").append(toIndentedString(typeCodeZka)).append("\n");
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
