/*
 * finAPI RESTful Services
 * finAPI RESTful Services
 *
 * OpenAPI spec version: v1.20.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * User details
 */
@ApiModel(description = "User details")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-05-17T14:26:47.047Z")
public class UserUpdateParamsImpl {
  @SerializedName("email")
  private String email = null;

  @SerializedName("phone")
  private String phone = null;

  @SerializedName("isAutoUpdateEnabled")
  private Boolean isAutoUpdateEnabled = false;

  public UserUpdateParamsImpl email(String email) {
    this.email = email;
    return this;
  }

   /**
   * User's new email address. Maximum length is 320. Pass an empty string (\"\") if you want to clear the current email address.
   * @return email
  **/
  @ApiModelProperty(example = "null", value = "User's new email address. Maximum length is 320. Pass an empty string (\"\") if you want to clear the current email address.")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserUpdateParamsImpl phone(String phone) {
    this.phone = phone;
    return this;
  }

   /**
   * User's new phone number. Maximum length is 50. Pass an empty string (\"\") if you want to clear the current phone number.
   * @return phone
  **/
  @ApiModelProperty(example = "null", value = "User's new phone number. Maximum length is 50. Pass an empty string (\"\") if you want to clear the current phone number.")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public UserUpdateParamsImpl isAutoUpdateEnabled(Boolean isAutoUpdateEnabled) {
    this.isAutoUpdateEnabled = isAutoUpdateEnabled;
    return this;
  }

   /**
   * Whether the user's bank connections will get updated in the course of finAPI's automatic batch update. Note that the automatic batch update will only update bank connections where all of the following applies:</br></br> - the PIN is stored in finAPI for the bank connection</br> - the previous update using the stored credentials did not fail due to the credentials being incorrect (or there was no previous update with the stored credentials)</br> - the bank that the bank connection relates to is included in the automatic batch update (please contact your Sys-Admin for details about the batch update configuration)</br></br>Also note that the automatic batch update must generally be enabled for your client in order for this field to have any effect.<br/><br/>WARNING: The automatic update will always download transactions and security positions for any account that it updates! This means that the user will no longer be able to download just the balances for his accounts once the automatic update has run (The 'skipPositionsDownload' flag in the bankConnections/update service can no longer be set to true).
   * @return isAutoUpdateEnabled
  **/
  @ApiModelProperty(example = "null", required = true, value = "Whether the user's bank connections will get updated in the course of finAPI's automatic batch update. Note that the automatic batch update will only update bank connections where all of the following applies:</br></br> - the PIN is stored in finAPI for the bank connection</br> - the previous update using the stored credentials did not fail due to the credentials being incorrect (or there was no previous update with the stored credentials)</br> - the bank that the bank connection relates to is included in the automatic batch update (please contact your Sys-Admin for details about the batch update configuration)</br></br>Also note that the automatic batch update must generally be enabled for your client in order for this field to have any effect.<br/><br/>WARNING: The automatic update will always download transactions and security positions for any account that it updates! This means that the user will no longer be able to download just the balances for his accounts once the automatic update has run (The 'skipPositionsDownload' flag in the bankConnections/update service can no longer be set to true).")
  public Boolean getIsAutoUpdateEnabled() {
    return isAutoUpdateEnabled;
  }

  public void setIsAutoUpdateEnabled(Boolean isAutoUpdateEnabled) {
    this.isAutoUpdateEnabled = isAutoUpdateEnabled;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserUpdateParamsImpl userUpdateParamsImpl = (UserUpdateParamsImpl) o;
    return Objects.equals(this.email, userUpdateParamsImpl.email) &&
        Objects.equals(this.phone, userUpdateParamsImpl.phone) &&
        Objects.equals(this.isAutoUpdateEnabled, userUpdateParamsImpl.isAutoUpdateEnabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, phone, isAutoUpdateEnabled);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserUpdateParamsImpl {\n");
    
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
    sb.append("    isAutoUpdateEnabled: ").append(toIndentedString(isAutoUpdateEnabled)).append("\n");
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
