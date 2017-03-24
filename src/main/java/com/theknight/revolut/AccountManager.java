package com.theknight.revolut;

import com.theknight.revolut.data.Account;
import com.theknight.revolut.data.CreationStatus;
import com.theknight.revolut.data.TransferStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {

    private Map<Integer, Account> accountMap = new ConcurrentHashMap<>();

    public Account getAccount(Integer accountId) {
        if (accountId != null) {
            return accountMap.get(accountId);
        } else {
            return null;
        }
    }

    public CreationStatus create(Integer accountId, BigDecimal initialAmount) {
        if (accountId != null && initialAmount != null && accountId > 0 && initialAmount.compareTo(BigDecimal.ZERO) >= 0) {
            if (accountMap.get(accountId) == null) {
                Account account = new Account();
                account.setId(accountId);
                account.setAmount(initialAmount);
                if (accountMap.putIfAbsent(accountId, account) == null) {
                    return CreationStatus.CREATED;
                } else {
                    return CreationStatus.ALREADY_EXIST;
                }
            } else {
                return CreationStatus.ALREADY_EXIST;
            }
        }
        return CreationStatus.WRONG_DATA;
    }

    public TransferStatus transfer(Integer fromId, Integer toId, BigDecimal amount) {
        if (fromId != null && toId != null && amount != null ) {

            if (fromId.equals(toId)) {
                return TransferStatus.REJECTED;
            }

            Account from = accountMap.get(fromId);
            Account to = accountMap.get(toId);

            if (from == null || to == null) {
                return TransferStatus.NOT_EXIST;
            }

            Account first;
            Account second;

            if (from.getId() < to.getId()) {
                first = from;
                second = to;
            } else {
                first = to;
                second = from;
            }

            try {
                first.lockAccount();
                try {
                    second.lockAccount();

                    if (from.getAmount().compareTo(amount) >= 0) {
                        from.setAmount(from.getAmount().subtract(amount));
                        to.setAmount(to.getAmount().add(amount));
                        return TransferStatus.SUCCESSFUL;
                    } else {
                        return TransferStatus.REJECTED;
                    }

                } finally {
                    second.unlockAccount();
                }
            } finally {
                first.unlockAccount();
            }
        }

        return TransferStatus.WRONG_DATA;
    }
}
