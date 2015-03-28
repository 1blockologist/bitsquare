/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.trade.protocol.trade.offerer.models;

import io.bitsquare.btc.AddressEntry;
import io.bitsquare.btc.WalletService;
import io.bitsquare.fiat.FiatAccount;
import io.bitsquare.offer.Offer;
import io.bitsquare.user.User;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.DeterministicKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import java.security.PublicKey;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Offerer implements Serializable {
    // That object is saved to disc. We need to take care of changes to not break deserialization.
    private static final long serialVersionUID = 1L;

    transient private static final Logger log = LoggerFactory.getLogger(Offerer.class);

    // Transient/Immutable
    private transient Offer offer;
    private transient WalletService walletService;
    private transient User user;

    // Mutable
    private byte[] payoutTxSignature;
    private Coin payoutAmount;
    private List<TransactionOutput> connectedOutputsForAllInputs;
    private List<TransactionOutput> outputs; // used to verify amounts with change outputs


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, initialization
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Offerer() {
        log.trace("Created by constructor");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        log.trace("Created from serialized form.");
    }

    // We need to wait until backend services are ready to set the required dependencies
    public void onAllServicesInitialized(Offer offer,
                                         WalletService walletService,
                                         User user) {
        log.trace("onBackendReady");
        this.offer = offer;
        this.walletService = walletService;
        this.user = user;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getter only
    ///////////////////////////////////////////////////////////////////////////////////////////

    public FiatAccount getFiatAccount() {
        return user.getFiatAccount(offer.getBankAccountId());
    }

    public String getAccountId() {
        return user.getAccountId();
    }

    public PublicKey getP2pSigPubKey() {
        return user.getP2PSigPubKey();
    }

    public PublicKey getP2pEncryptPubKey() {
        return user.getP2PEncryptPubKey();
    }

    public byte[] getRegistrationPubKey() {
        return walletService.getRegistrationAddressEntry().getPubKey();
    }

    public DeterministicKey getRegistrationKeyPair() {
        return walletService.getRegistrationAddressEntry().getKeyPair();
    }

    public AddressEntry getAddressEntry() {
        return walletService.getAddressEntry(offer.getId());
    }

    public byte[] getTradeWalletPubKey() {
        return getAddressEntry().getPubKey();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getter/Setter for Mutable objects
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    @Nullable
    public byte[] getPayoutTxSignature() {
        return payoutTxSignature;
    }

    public void setPayoutTxSignature(byte[] payoutTxSignature) {
        this.payoutTxSignature = payoutTxSignature;
    }

    @Nullable
    public Coin getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(Coin payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    @Nullable
    public List<TransactionOutput> getConnectedOutputsForAllInputs() {
        return connectedOutputsForAllInputs;
    }

    public void setConnectedOutputsForAllInputs(List<TransactionOutput> connectedOutputsForAllInputs) {
        this.connectedOutputsForAllInputs = connectedOutputsForAllInputs;
    }

    @Override
    public String toString() {
        return "Offerer{" +
                "offer=" + offer +
                ", walletService=" + walletService +
                ", user=" + user +
                ", payoutTxSignature=" + Arrays.toString(payoutTxSignature) +
                ", payoutAmount=" + payoutAmount +
                ", connectedOutputsForAllInputs=" + connectedOutputsForAllInputs +
                ", outputs=" + outputs +
                '}';
    }
}