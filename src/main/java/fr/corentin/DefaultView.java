package fr.corentin.views;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class DefaultView implements IView {

        protected final Stage _stage;

        public DefaultView(Stage stage) {
            this._stage = stage;
        }

        @Override
        public abstract DefaultView build();

        void setContent(Parent root) { this._stage.setScene(new Scene(root)); }
}