package mop.app.client.util;

import lombok.Getter;

@Getter
public class ViewModel {
    private final ViewFactory viewFactory;

    public ViewModel() {
        viewFactory = new ViewFactory();
    }

    public ViewModel(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    private static final class ViewModelHolder {
        private static final ViewModel INSTANCE = new ViewModel();
    }

    public static ViewModel getInstance() {
        return ViewModelHolder.INSTANCE;
    }
}
