package romanow.abc.ess2.android.service;

import romanow.abc.core.UniException;
import romanow.abc.core.drivers.I_ModbusGroupDriver;

public interface I_ModbusGroupAsyncDriver extends I_ModbusGroupDriver {
    public void readRegister(final String devName,final int unit, final int regNum, NetBack back);
    public void writeRegister(final String devName,final int unit, final int regNum, final int value, NetBack back);
    }
