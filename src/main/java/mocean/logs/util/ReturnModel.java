package mocean.logs.util;

import java.io.Serializable;

public class ReturnModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String returnCode;
    private String returnMessage;
    public String getReturnCode() {
        return returnCode;
    }
    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }
    public String getReturnMessage() {
        return returnMessage;
    }
    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

}