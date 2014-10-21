package org.eclipse.emf.edit.ui.addon;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * Use this {@link AdapterFactoryContentProvider} if the viewer's underlying domain model frequently
 * changes and would produce to many notification events. In that case it's much more efficient to
 * trigger a full refresh after a short delay.
 * 
 * @author Wolfgang Geck
 * 
 */
public class DelayedAdapterFactoryContentProvider extends AdapterFactoryContentProvider {

    private static final int MAX_INTERVAL_TIME = 500;

    private final AtomicBoolean refresh = new AtomicBoolean(false);
    private Viewer viewer;
    private int intervalTime;

    public DelayedAdapterFactoryContentProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    @Override
    public void notifyChanged(Notification notification) {

        if (!notification.isTouch() && !refresh.getAndSet(true)) {

            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {

                    Display.getDefault().timerExec(intervalTime, new Runnable() {

                        @Override
                        public void run() {

                            intervalTime = MAX_INTERVAL_TIME; // after first update we will wait a little longer 

                            refresh.set(false);

                            if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
                                viewer.refresh();
                            }
                        }

                    });

                }

            });

        }

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = viewer;
        super.inputChanged(viewer, oldInput, newInput);
    }

}
