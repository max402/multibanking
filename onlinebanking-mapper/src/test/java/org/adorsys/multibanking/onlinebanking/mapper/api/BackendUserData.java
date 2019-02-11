package org.adorsys.multibanking.onlinebanking.mapper.api;

import lombok.Data;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.UserData;

/**
 * Created by peter on 11.02.19 12:06.
 */
@Data
public class BackendUserData extends UserData {
    String someBackendInfo;
}
