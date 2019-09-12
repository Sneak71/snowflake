package snowflake.components.files.browser.local;

import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.local.files.LocalFileSystem;
import snowflake.components.common.AddressBar;
import snowflake.components.files.DndTransferData;
import snowflake.components.files.DndTransferHandler;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.AbstractFileBrowserView;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalFileBrowserView extends AbstractFileBrowserView {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private LocalMenuHandler menuHandler;
    private FileBrowser fileBrowser;
    private DndTransferHandler transferHandler;
    private LocalFileSystem fs;
    private JPopupMenu addressPopup;

    public LocalFileBrowserView(FileBrowser fileBrowser,
                                JRootPane rootPane, FileComponentHolder holder, String initialPath, PanelOrientation orientation) {
        super(rootPane, holder, orientation, fileBrowser);
        this.fileBrowser = fileBrowser;
        this.menuHandler = new LocalMenuHandler(fileBrowser, this, holder);
        this.menuHandler.initMenuHandler(this.folderView);
        this.transferHandler = new DndTransferHandler(this.folderView, null, this);
        this.folderView.setTransferHandler(transferHandler);
        this.folderView.setFolderViewTransferHandler(transferHandler);
        this.addressPopup = menuHandler.createAddressPopup();
        if (initialPath != null) {
            this.path = initialPath;
        }
        executor.submit(() -> {
            try {
                this.fs = new LocalFileSystem();
                if (this.path == null) {
                    path = fs.getHome();
                }
                List<FileInfo> list = fs.list(path);
                SwingUtilities.invokeLater(() -> {
                    addressBar.setText(path);
                    folderView.setItems(list);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void createAddressBar() {
        addressBar = new AddressBar(File.separatorChar, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPath = e.getActionCommand();
                addressPopup.setName(selectedPath);
                MouseEvent me = (MouseEvent) e.getSource();
                addressPopup.show(me.getComponent(), me.getX(), me.getY());
                System.out.println("clicked");
            }
        });
    }

    @Override
    public String toString() {
        return "Local files [" + this.path + "]";
    }


    @Override
    public void render(String path) {
        this.path = path;
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (this.path == null) {
                    this.path = fs.getHome();
                }
                List<FileInfo> list = fs.list(this.path);
                SwingUtilities.invokeLater(() -> {
                    addressBar.setText(this.path);
                    folderView.setItems(list);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileBrowser.enableUi();
        });
    }

    @Override
    public void openApp(FileInfo file) {

    }

    @Override
    public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
        menuHandler.createMenu(popup, files);
        return true;
    }

    protected void up() {
        String s = new File(path).getParent();
        if (s != null) {
            addBack(path);
            render(s);
        }
    }

    protected void home() {
        addBack(path);
        render(null);
    }

    @Override
    public void install(JComponent c) {

    }

    public boolean handleDrop(DndTransferData transferData) {
        try {
            System.out.println("Dropped: " + transferData);
            int sessionHashCode = transferData.getInfo();
            if (sessionHashCode == 0) return true;
            SessionInfo info = holder.getInfo();
            if (info != null && info.hashCode() == sessionHashCode) {
                FileSystem sourceFs = holder.getSshFileSystem();
                if (sourceFs == null) {
                    return false;
                }
                FileSystem targetFs = this.fs;
                holder.newFileTransfer(sourceFs, targetFs, transferData.getFiles(), transferData.getCurrentDirectory(), this.path, this.hashCode());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}