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

import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.ForeignPayment;
import lombok.RequiredArgsConstructor;
import org.kapott.hbci.GV.AbstractHBCIJob;
import org.kapott.hbci.GV.GVDTAZV;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.passport.PinTanPassport;
import org.kapott.hbci.structures.Konto;

@RequiredArgsConstructor
public class ForeignPaymentJob extends AbstractPaymentJob<ForeignPayment> {

    private final TransactionRequest<ForeignPayment> transactionRequest;
    private GVDTAZV hbciForeignPaymentJob;

    @Override
    public AbstractHBCIJob createJobMessage(PinTanPassport passport) {
        Konto src = getPsuKonto(passport);

        hbciForeignPaymentJob = new GVDTAZV(passport, GVDTAZV.getLowlevelName());

        hbciForeignPaymentJob.setParam("src", src);
        hbciForeignPaymentJob.setParam("dtazv", "B" + transactionRequest.getTransaction().getRawRequestData());
        hbciForeignPaymentJob.verifyConstraints();

        return hbciForeignPaymentJob;
    }

    @Override
    AbstractHBCIJob getHbciJob() {
        return hbciForeignPaymentJob;
    }

    @Override
    TransactionRequest<ForeignPayment> getTransactionRequest() {
        return transactionRequest;
    }

    @Override
    protected String getHbciJobName(AbstractTransaction.TransactionType transactionType) {
        return GVDTAZV.getLowlevelName();
    }

    @Override
    public String orderIdFromJobResult(HBCIJobResult paymentGV) {
        return null;
    }
}
