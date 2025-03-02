/*
 * Copyright 2018-2019 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.multibanking.hbci.job;

import de.adorsys.multibanking.domain.AbstractScaTransaction;
import de.adorsys.multibanking.domain.RawSepaPayment;
import org.apache.commons.lang3.math.NumberUtils;
import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.GVDauerSEPANew;
import org.kapott.hbci.GV.GVRawSEPA;
import org.kapott.hbci.GV.GVUebSEPA;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.comm.CommPinTan;
import org.kapott.hbci.passport.PinTanPassport;
import org.kapott.hbci.sepa.SepaVersion;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RawSepaJob extends ScaRequiredJob {

    @Override
    String getHbciJobName(AbstractScaTransaction.TransactionType paymentType) {
        return GVRawSEPA.getLowlevelName();
    }

    @Override
    String orderIdFromJobResult(HBCIJobResult jobResult) {
        return null;
    }

    @Override
    AbstractSEPAGV createHbciJob(AbstractScaTransaction transaction, PinTanPassport passport) {
        RawSepaPayment sepaPayment = (RawSepaPayment) transaction;

        String jobName;
        switch (sepaPayment.getSepaTransactionType()) {
            case SINGLE_PAYMENT:
                jobName = GVUebSEPA.getLowlevelName();
                break;
            case BULK_PAYMENT:
                jobName = "SammelUebSEPA";
                break;
            case STANDING_ORDER:
                jobName = GVDauerSEPANew.getLowlevelName();
                break;
            default:
                throw new IllegalArgumentException("unsupported raw sepa transaction: " + transaction.getTransactionType());
        }

        GVRawSEPA sepagv = new GVRawSEPA(passport, jobName, sepaPayment.getRawData());
        sepagv.setParam("src", getDebtorAccount(transaction, passport));

        appendPainValues(sepaPayment, sepagv);

        sepagv.verifyConstraints();

        return sepagv;
    }

    private void appendPainValues(RawSepaPayment sepaPayment, GVRawSEPA sepagv) {
        String creditorIban = "";
        BigDecimal amount = new BigDecimal(0);
        String currency = "";

        List<Map<String, String>> result = parsePain(sepaPayment.getPainXml());
        for (Map<String, String> resultMap : result) {
            creditorIban = resultMap.get("dst.iban");
            amount = amount.add(NumberUtils.createBigDecimal(resultMap.get("value")));

            String tempCurrency = resultMap.get("curr");
            if (currency.length() > 0 && !currency.equals(tempCurrency)) {
                throw new IllegalArgumentException("mixed currencies in bulk payment");
            }
            currency = tempCurrency;
        }

        if (result.size() > 1) {
            sepagv.setLowlevelParam(sepagv.getName() + ".sepa.dst.iban", creditorIban);
        }

        sepagv.setLowlevelParam(sepagv.getName() + ".sepa.btg.value", amount.toString());
        sepagv.setLowlevelParam(sepagv.getName() + ".sepa.btg.curr", currency);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> parsePain(String painXml) {
        List<Map<String, String>> sepaResults = new ArrayList<>();
        ISEPAParser<List<Map<String, String>>> parser =
                SEPAParserFactory.get(SepaVersion.autodetect(painXml));
        try {
            parser.parse(new ByteArrayInputStream(painXml.getBytes(CommPinTan.ENCODING)), sepaResults);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return sepaResults;
    }
}
