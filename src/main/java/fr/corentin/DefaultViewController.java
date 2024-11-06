package fr.corentin.controllers.viewcontrollers;

import fr.corentin.controllers.MainController;

public abstract class DefaultViewController implements IViewController {

    protected final MainController _main;

    public DefaultViewController(MainController main) {
        this._main = main;
    }

    public void back() { this._main.back(); }
}