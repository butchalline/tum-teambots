package teambot.common.interfaces;

public interface IPoseChangeSupplier extends IPoseSupplier {

	public void registerForChangeUpdates(IPoseChangeListener listener);
	public void unregisterForChangeUpdates(IPoseChangeListener listener);
}
