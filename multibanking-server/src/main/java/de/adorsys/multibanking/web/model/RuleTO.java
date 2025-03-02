package de.adorsys.multibanking.web.model;

import lombok.Data;
import org.springframework.hateoas.core.Relation;

@Relation(collectionRelation = "ruleList")
@Data
public class RuleTO {

    private String id;
    private String ruleId;
    private String mainCategory;
    private String subCategory;
    private String specification;
    private SIMILARITY_MATCH_TYPE similarityMatchType;
    private String creditorId;
    private String expression;
    private String receiver;
    private String ruleType;
    private String logo;
    private String hotline;
    private String homepage;
    private String email;
    private boolean incoming;

    public enum SIMILARITY_MATCH_TYPE {
        IBAN, REFERENCE_NAME, PURPOSE
    }

}
