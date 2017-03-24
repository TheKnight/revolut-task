package com.theknight.revolut;

import com.theknight.revolut.data.CreationStatus;
import com.theknight.revolut.data.TransferStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class AccountManagerTest {

    private AccountManager accountManager;

    @Before
    public void setUp() {
        accountManager = new AccountManager();
    }

    @Test
    public void createValidAccount() {
        CreationStatus creationStatus = accountManager.create(1, new BigDecimal(100));
        Assert.assertEquals(CreationStatus.CREATED, creationStatus);
    }

    @Test
    public void createAlreadyExistedAccount() {
        accountManager.create(1, new BigDecimal(120));
        CreationStatus creationStatus = accountManager.create(1, new BigDecimal(100));
        Assert.assertEquals(CreationStatus.ALREADY_EXIST, creationStatus);
    }

    @Test
    public void createNonValidAccount() {
        CreationStatus creationStatus = accountManager.create(-1, new BigDecimal(100));
        Assert.assertEquals(CreationStatus.WRONG_DATA, creationStatus);
    }

    @Test
    public void transferSuccessful() {
        CreationStatus creationStatus = accountManager.create(1, new BigDecimal(100));
        Assert.assertEquals(CreationStatus.CREATED, creationStatus);
        creationStatus = accountManager.create(2, new BigDecimal(5));
        Assert.assertEquals(CreationStatus.CREATED, creationStatus);

        TransferStatus transferStatus = accountManager.transfer(1, 2, new BigDecimal(10));
        Assert.assertEquals(TransferStatus.SUCCESSFUL, transferStatus);
    }

    @Test
    public void transferRejected() {
        CreationStatus creationStatus = accountManager.create(1, new BigDecimal(100));
        Assert.assertEquals(CreationStatus.CREATED, creationStatus);
        creationStatus = accountManager.create(2, new BigDecimal(5));
        Assert.assertEquals(CreationStatus.CREATED, creationStatus);

        TransferStatus transferStatus = accountManager.transfer(2, 1, new BigDecimal(10));
        Assert.assertEquals(TransferStatus.REJECTED, transferStatus);
    }

    @Test
    public void transferSameRejected() {
        CreationStatus creationStatus = accountManager.create(1, new BigDecimal(100));
        Assert.assertEquals(CreationStatus.CREATED, creationStatus);

        TransferStatus transferStatus = accountManager.transfer(1, 1, new BigDecimal(10));
        Assert.assertEquals(TransferStatus.REJECTED, transferStatus);

    }

}
