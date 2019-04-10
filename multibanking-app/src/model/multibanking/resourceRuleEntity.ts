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
import { Link } from './link';


export interface ResourceRuleEntity { 
    links?: Array<Link>;
    creditorId?: string;
    custom?: { [key: string]: string; };
    email?: string;
    expression?: string;
    homepage?: string;
    hotline?: string;
    id?: string;
    incoming?: boolean;
    logo?: string;
    mainCategory?: string;
    order?: number;
    receiver?: string;
    ruleId?: string;
    searchIndex?: Array<string>;
    similarityMatchType?: ResourceRuleEntity.SimilarityMatchTypeEnum;
    specification?: string;
    stop?: boolean;
    subCategory?: string;
    userId?: string;
}
export namespace ResourceRuleEntity {
    export type SimilarityMatchTypeEnum = 'IBAN' | 'REFERENCE_NAME' | 'PURPOSE' | 'CUSTOM';
    export const SimilarityMatchTypeEnum = {
        IBAN: 'IBAN' as SimilarityMatchTypeEnum,
        REFERENCENAME: 'REFERENCE_NAME' as SimilarityMatchTypeEnum,
        PURPOSE: 'PURPOSE' as SimilarityMatchTypeEnum,
        CUSTOM: 'CUSTOM' as SimilarityMatchTypeEnum
    };
}
