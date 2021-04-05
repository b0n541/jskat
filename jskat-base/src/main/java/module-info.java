module jskat.base {
    requires java.desktop;

    requires org.slf4j;
    requires com.google.common;

    exports org.jskat.control;
    exports org.jskat.control.command.iss;
    exports org.jskat.control.command.general;
    exports org.jskat.control.command.table;
    exports org.jskat.control.command.skatseries;
    exports org.jskat.control.event.iss;
    exports org.jskat.control.event.general;
    exports org.jskat.control.event.skatgame;
    exports org.jskat.control.event.table;
    exports org.jskat.control.gui;
    exports org.jskat.control.gui.action;
    exports org.jskat.control.gui.img;
    exports org.jskat.control.gui.human;
    exports org.jskat.control.iss;
    exports org.jskat.data;
    exports org.jskat.data.iss;
    exports org.jskat.player;
    exports org.jskat.util;
    exports org.jskat.util.version;
}