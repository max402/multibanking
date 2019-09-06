/**
 * Multibanking REST Api
 * Use a bank code (blz) ending with X00 000 00 like 300 000 00 to run aggainst the mock backend. Find the mock backend at ${hostname}:10010
 *
 * OpenAPI spec version: 5.1.3-SNAPSHOT
 * Contact: age@adorsys.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { Link } from './link';


export interface ResourceRuleTO { 
    links?: Array<Link>;
    creditorId?: string;
    email?: string;
    expression?: string;
    homepage?: string;
    hotline?: string;
    id?: string;
    incoming?: boolean;
    logo?: string;
    mainCategory?: string;
    receiver?: string;
    ruleId?: string;
    ruleType?: string;
    similarityMatchType?: ResourceRuleTO.SimilarityMatchTypeEnum;
    specification?: string;
    subCategory?: string;
}
export namespace ResourceRuleTO {
    export type SimilarityMatchTypeEnum = 'IBAN' | 'REFERENCE_NAME' | 'PURPOSE';
    export const SimilarityMatchTypeEnum = {
        IBAN: 'IBAN' as SimilarityMatchTypeEnum,
        REFERENCENAME: 'REFERENCE_NAME' as SimilarityMatchTypeEnum,
        PURPOSE: 'PURPOSE' as SimilarityMatchTypeEnum
    };
}
