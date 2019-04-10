/**
 * Multibanking REST Api
 * Use a bank code (blz) ending with X00 000 00 like 300 000 00 to run aggainst the mock backend. Find the mock backend at ${hostname}:10010
 *
 * OpenAPI spec version: 4.2.1-SNAPSHOT
 * Contact: age@adorsys.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { Consent } from './consent';
import { TanTransportType } from './tanTransportType';


/**
 * BankAccess account information
 */
export interface BankAccess { 
    /**
     * Bank code
     */
    bankCode: string;
    /**
     * Bank login name
     */
    bankLogin: string;
    /**
     * 2nd bank login name
     */
    bankLogin2?: string;
    /**
     * Bank name
     */
    bankName?: string;
    categorizeBookings?: boolean;
    /**
     * SCA consent
     */
    consent?: Consent;
    /**
     * IBAN
     */
    iban: string;
    id?: string;
    pin?: string;
    pin2?: string;
    provideDataForMachineLearning?: boolean;
    storeAnalytics?: boolean;
    storeAnonymizedBookings?: boolean;
    storeBookings?: boolean;
    storePin?: boolean;
    /**
     * Supported tan transport types
     */
    tanTransportTypes?: { [key: string]: Array<TanTransportType>; };
    temporary?: boolean;
    userId?: string;
}
