package org.leo.server.panama.core.connector.impl;

public class UpgradeResponse extends HttpResponse {
    private boolean upgrade;

    public UpgradeResponse(String message, boolean upgrade) {
        super(message);
        this.upgrade = upgrade;
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }
}
