package manager;

import util.clogger.LogBuffer;

public interface CIManager {
	public boolean isRunnable();
	public boolean initialize(LogBuffer buf);
	public String getType();
}
