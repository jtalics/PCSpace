package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Purpose: allows the user to specify multiple directories/folders on this computer
 * 
 */
final class DirChooserPanel extends JPanel {

    /**
     * Purpose: wrapper for tree cell renderer
     * 
     */
    private static final class Dir extends File {

        // Was lazily loaded already
        boolean childrenLoaded = false;
        boolean descendantsLoaded = false;
        protected Icon icon;

        public Dir(final String pathname) {
            super(pathname);
        }

        @Override
        public String toString() {
            if (getName() == null || getName().equals("")) {
                return super.toString();
            }
            return this.getName();
        }

        private static final long serialVersionUID = 1L;
        
        public Icon getIcon() {
            if (icon == null) {
                icon = fileSystemView.getSystemIcon(this);
            }
            return icon;
        }
    }

    private static final class FileAndIcon {

        File file;
        Icon icon;

        public FileAndIcon(final File file) {
            super();
            this.file = file;
            this.icon = FileSystemView.getFileSystemView().getSystemIcon(file);
        }
    }

    private final static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    private final JTree tree;
    private final CheckTreeManager checkTreeManager;
    private final CheckTreeSelectionModelB checkTreeSelectionModel;
    private final CheckTreeModel checkTreeModel;
    private boolean recurseSelection;
    private static final boolean debug = false;
    private static final boolean osOk = System.getProperty("os.name").equals("Windows XP");

    /**
     * @param owner
     * @param initialPaths
     */
    DirChooserPanel(final Window owner, final SelectionMode selectionMode, final List<String> initialPaths) {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(500, 310));
        final JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
        splitPane.setOneTouchExpandable(true);
        final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(null);
        owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final File[] roots = File.listRoots();
        for (final File root : roots) {
            if (!root.exists()) {
                continue;
            }
            final DefaultMutableTreeNode disk = new DefaultMutableTreeNode(new Dir(root.getPath()));
            treeRoot.add(disk);
            loadChildren(disk, false, 0);
            loadGrandchildren(disk);
        }
        owner.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        checkTreeModel = new CheckTreeModel(treeRoot);
        tree = new JTree(checkTreeModel);

        tree.setCellRenderer(new TreeCellRenderer() {

            @Override
						public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
                final Dir dir = (Dir) ((DefaultMutableTreeNode) value).getUserObject();
                final JLabel label = new JLabel(dir.toString());
                if (dir.exists()) {
                    label.setIcon(dir.getIcon());
                    //Icon icon2=fileSystemView.getSystemIcon(dir);
                    //if (icon2 != null) {label.setIcon(icon2);}
                }
                return label;
            }

        });

        expandJTreeNode(tree, tree.getModel(), tree.getModel().getRoot(), 0, 1);

        tree.addTreeExpansionListener(new TreeExpansionListener() {

            @Override
						public void treeCollapsed(final TreeExpansionEvent event) {
            // Do not remove (selected) nodes because they are needed for the final returned paths
            }

            // Called when user clicks
            @Override
						public void treeExpanded(final TreeExpansionEvent event) {
                // Lazy load
                final TreePath expandedPath = event.getPath();
                final DefaultMutableTreeNode expandedNode = (DefaultMutableTreeNode) expandedPath.getLastPathComponent();
                // Load only upto grandchildren (because the node needs to know whether it can be expanded),
                // but *only* upto grandchildren because we must *lazy* load
                loadGrandchildren(expandedNode);
            }
        });

        tree.setRootVisible(false);

        checkTreeSelectionModel = new CheckTreeSelectionModelB(checkTreeModel, TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        final List<String> pathsNotFound = setInitialSelections(checkTreeSelectionModel, initialPaths);
        if (pathsNotFound != null && pathsNotFound.size() > 0) {
            final StringBuffer sb = new StringBuffer();
            sb.append("Path(s) not found:\n");
            for (final String pathNotFound : pathsNotFound) {
                sb.append(pathNotFound);
                sb.append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        }

        checkTreeManager = new CheckTreeManager(tree, checkTreeModel, checkTreeSelectionModel);

        JScrollPane scrollPane = new JScrollPane(tree);
        splitPane.setTopComponent(scrollPane);

        final JList list = new JList();
        final DefaultListModel defaultListModel = new DefaultListModel();
        list.setModel(defaultListModel);

        list.setCellRenderer(new ListCellRenderer() {

            private final JLabel label = new JLabel();

            @Override
						public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
                label.setText(((DirChooserPanel.FileAndIcon) value).file.getName());
                label.setIcon(((DirChooserPanel.FileAndIcon) value).icon);
                label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
                return label;
            }
        });

        list.setEnabled(false);
        list.setBackground(new Color(240, 240, 240));
        scrollPane = new JScrollPane(list);
        splitPane.setBottomComponent(scrollPane);

        checkTreeSelectionModel.addTreeSelectionListener(new TreeSelectionListener() {

            // update the files area
            @Override
						public void valueChanged(final TreeSelectionEvent e) {
                final TreePath selectedPath = e.getPath();
                final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                if (debug) {
                    System.out.println("You checked = " + selectedNode + " state is " + checkTreeSelectionModel.isPathSelected(selectedPath));
                }
                if (recurseSelection) {
                    final boolean isPathSelected = checkTreeSelectionModel.isPathSelected(selectedPath);
                    if (isPathSelected) {
                        // No need to loadChildren if they aren't already, just to deselect them.
                        // Date start = new Date();
                        DirChooserPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (osOk) { // WINDOWS (FASTER)
                            int dirCount = 0;
                            final List<String> pathList = new ArrayList<String>();
                            try {
                                String s = ((File) selectedNode.getUserObject()).getPath();
                                final BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("cmd.exe /C cd " + s + " & cd & dir/s/boundingBox/a:d ").getInputStream()));
                                while (null != (s = br.readLine())) {
                                    pathList.add(s);
                                    dirCount++;
                                }
                            } catch (final IOException e1) {
                                e1.printStackTrace();
                            }

//                            if (dirCount >= 10) {
//                                int answer = JOptionPane.showConfirmDialog(DirChooserPanel.this, "Really load " + dirCount + " directories?");
//                                if (answer != JOptionPane.YES_OPTION) {
//                                    return;
//                                }
//                            }
                            DirChooserPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            loadDescendants(selectedNode, pathList.toArray(new String[pathList.size()]));
                        }
                        else { // PLATFORM INDEPENDENT (SLOWER)
                            // If the user is recursing, all subdirectories must be selected immediately
                            // Eagerly load all descendants
                            loadChildren(selectedNode, true, 0);
                        }
                        DirChooserPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        // System.out.println("Elapsed time = " + (new Date().getTime() - start.getTime()));
                    }
                    // Propagate the selection state to the new descendants.
                    // No need for us to listen since we originated the selection event ourselves.
                    checkTreeSelectionModel.removeTreeSelectionListener(this);
                    setSelectionForDescendants(selectedPath, checkTreeSelectionModel.isPathSelected(selectedPath));
                    checkTreeSelectionModel.addTreeSelectionListener(this);
                    tree.treeDidChange();
                }
            }
        });

        // Add the selection listener that handles the tree row clicks and loads up the right panel with filenames and icons
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            // update the files area
            @Override
						public void valueChanged(final TreeSelectionEvent e) {
                DirChooserPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                final TreePath selectedPath = e.getPath();
                final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                // We now have a directory, so get its contents and display at the right
                defaultListModel.clear();
                final File[] dirList = ((File) selectedNode.getUserObject()).listFiles();
                if (dirList != null) {
                    for (final File file : dirList) {
                        defaultListModel.addElement(new FileAndIcon(file));
                    }
                }
                DirChooserPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            }
        });
    }

    private void setSelectionForDescendants(final TreePath parentPath, final boolean isSelected) {
        List<TreePath> paths = new ArrayList<TreePath>();
        paths = getDescendantPaths(parentPath, paths);
        if (debug) {
            System.out.println("DESCENDANTS OF : " + parentPath);
            for (final TreePath path : paths) {
                System.out.println("DESC: " + path);
            }
        }
        final TreePath[] array = paths.toArray(new TreePath[] {});
        if (isSelected) {
            checkTreeSelectionModel.addSelectionPaths(array);
        }
        else {
            checkTreeSelectionModel.removeSelectionPaths(array);
        }
    }

    private void loadGrandchildren(final DefaultMutableTreeNode grandparentNode) {
        // Using this method implies you are lazy loading
        final Dir grandDir = (Dir) grandparentNode.getUserObject();
        if (debug) {
          System.out.println("loading grandchildren in: " + grandparentNode);
        }
        if (!grandDir.childrenLoaded) {
            // don't forget to call loadChildren() first
            throw new RuntimeException("cannot get grandchildren without children first");
        }

        for (int i = 0; i < grandparentNode.getChildCount(); i++) {
            final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) grandparentNode.getChildAt(i);
            final Dir parentDir = (Dir) parentNode.getUserObject();
            if (parentDir.childrenLoaded) {
                if (debug) {
                  System.out.println("children already loaded: " + grandparentNode);
                }
                continue;
            }
            loadChildren(parentNode, false, 0);
            parentDir.childrenLoaded = true;
        }
        // checkTreeModel.nodeStructureChanged(node);
    }

    private int loadChildren(final DefaultMutableTreeNode parentNode, final boolean eagerLoading, int currentLoadCount) {
        // Parent node must already be loaded.
        // Loading a node means to get File info from the OS
        // and put it inside the node as a user object.
        final Dir parentDir = (Dir) parentNode.getUserObject();
        if (eagerLoading) {
            // We want to load all descendants to the given node.
            if (parentDir.descendantsLoaded) {
                if (debug) {
                  System.out.println("node already eager loaded: " + parentNode);
                }
                // All nodes below this node have been loaded already.
                return currentLoadCount;
            }
            if (debug) {
              System.out.println("Eagerly loading: " + parentNode);
            }
            parentDir.descendantsLoaded = true;
        }
        if (parentDir.childrenLoaded) {
            // This node was already lazily loaded
            if (debug) {
              System.out.println("children already loaded: " + parentNode);
            }
            if (eagerLoading) {
                // Continue recursively loading on already loaded children, skipping over this generation
                for (int i = 0; i < parentNode.getChildCount(); i++) {
                    final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                    currentLoadCount = loadChildren(childNode, true, currentLoadCount);
                }
            }
        }
        else {
            if (debug) {
              System.out.println("loading children to: " + parentNode);
            }
            final File[] tmp = parentDir.listFiles();
            if (tmp == null) {
                // Nothing in this directory
                return currentLoadCount;
            }
            // Sort the contents into alphabetical order
            // Vector<File> dirContents = new Vector<File>();
            // for (int i = 0; i < tmp.length; i++) {
            // dirContents.addElement(tmp[i]);
            // }
            // TODO: Collections.sort(dirContents, String.CASE_INSENSITIVE_ORDER);

            // For each directory in this directory
            for (final File file : tmp) {
                if (file.isDirectory()) {
                    final DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(new Dir(file.getPath()));
                    parentNode.add(dirNode);
                    currentLoadCount++;
                    if (debug) {
                      System.out.println("Created node " + currentLoadCount + ": " + ((Dir) dirNode.getUserObject()).getAbsolutePath());
                    }
                    if (eagerLoading) {
                        currentLoadCount = loadChildren(dirNode, true, currentLoadCount);
                    }
                }
            }
            // checkTreeModel.nodeStructureChanged(currentNode);
            parentDir.childrenLoaded = true;
        }

        if (debug) {
          System.out.println("loading children in: " + parentNode);
        }
        return currentLoadCount;
    }

    private void loadDescendants(final DefaultMutableTreeNode ancestorNode, final String[] descendantPaths) {
        if (debug) {
            System.out.println("Loading decendants...");
        }
        final Dir parentDir = (Dir) ancestorNode.getUserObject();
        // We want to load all descendants to the given node.
        if (parentDir.descendantsLoaded) {
            // All nodes below this node have been loaded already.
            return;
        }
        // Sort into depth-first traversal order
        Arrays.sort(descendantPaths);
        if (debug) {
            for (final String descendantPath : descendantPaths) {
                System.out.println(descendantPath);
            }
        }
        int childLevel;
        DefaultMutableTreeNode prevNode = ancestorNode;
        DefaultMutableTreeNode childNode = null;
        // Turn each path string into a node and connect it to the tree
        for (int i = 1; i < descendantPaths.length; i++) {
            final int prevNodeLevel = prevNode.getLevel();
            final String s = descendantPaths[i];
            // Determine the level of this node
            childLevel = 1;
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '\\') {
                    childLevel++;
                }
            }
            if (debug) {
                System.out.println("\nNew path = " + descendantPaths[i] + " level = " + childLevel + " previous node = " + prevNode + " at Level = " + prevNodeLevel);
            }
            // We need to connect the childNode to the growing tree.
            if (childLevel == prevNodeLevel) {
                final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) prevNode.getParent();
                final Dir dir = (Dir) parentNode.getUserObject();
                if (dir.childrenLoaded) {
                    if (null == (childNode = getLoadedNode(parentNode, descendantPaths[i]))) {
                        throw new RuntimeException("parent " + parentNode + " missing child node " + descendantPaths[i]);
                    }
                    // Found it, use this node. This node is already connected.
                    if (debug) {
                        System.out.println("  child node = " + childNode + " found to be loaded in node = " + parentNode + "which is at level = " + parentNode.getLevel());
                    }
                }
                else {
                    // Create the child node and connect in
                    childNode = new DefaultMutableTreeNode(new Dir(descendantPaths[i]));
                    parentNode.add(childNode);
                    if (debug) {
                        System.out.println("Added child node = " + childNode + " to node = " + parentNode);
                    }
                }
            }
            else if (childLevel == prevNodeLevel + 1) {
                // Next deeper level. Previous node is parent to child.
                final Dir dir = (Dir) prevNode.getUserObject();
                if (dir.childrenLoaded) {
                    // Parent says this node was lazily loaded.
                    // We don't want to create any nodes until we get to the level where they stopped loading.
                    // Validate existing node.
                    if (null == (childNode = getLoadedNode(prevNode, descendantPaths[i]))) {
                        throw new RuntimeException("parent " + prevNode + " missing child node " + descendantPaths[i]);
                    }
                    // Found it, use this node. This node is already connected.
                    if (debug) {
                        System.out.println("  child node exists at level = " + prevNode.getLevel());
                    }
                }
                else {
                    // Create the child node and connect in
                    childNode = new DefaultMutableTreeNode(new Dir(descendantPaths[i]));
                    prevNode.add(childNode);
                    if (debug) {
                        System.out.println("Descended and added new child to " + prevNode);
                    }
                }
            }
            else if (childLevel < prevNodeLevel) {
                // Finished this level.
                // Before we retreat, record that everything is loaded under the parent.
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) prevNode.getParent();
                Dir dir = null;
                // Go up the proper number of levels and connect child
                for (int k = prevNodeLevel; k > childLevel; k--) {
                    dir = (Dir) parentNode.getUserObject();
                    dir.descendantsLoaded = true;
                    dir.childrenLoaded = true;
                    if (debug) {
                        System.out.println("Marked as finished: " + parentNode);
                    }
                    parentNode = (DefaultMutableTreeNode) parentNode.getParent();
                }
                dir = (Dir) parentNode.getUserObject();
                if (dir.childrenLoaded) {
                    // Parent says this node was lazily loaded.
                    // We don't want to create any nodes until we get to the level where they stopped loading.
                    // Validate existing node.
                    if (null == (childNode = getLoadedNode(parentNode, descendantPaths[i]))) {
                        throw new RuntimeException("parent " + parentNode + " missing child node " + descendantPaths[i]);
                        // TODO: Make a new child node anyway?
                    }
                    // Found it, use this node. This node is already connected.
                    if (debug) {
                        System.out.println("  child node exists at level = " + prevNode.getLevel());
                    }
                }
                else {
                    childNode = new DefaultMutableTreeNode(new Dir(descendantPaths[i]));
                    parentNode.add(childNode);
                    if (debug) {
                        System.out.println("Ascended and added new child to new parent: " + parentNode);
                    }
                }
            }
            else {
                throw new RuntimeException("bad level: " + childLevel);
            }
            prevNode = childNode;
        }
        // This method is called only for eager loading
        parentDir.descendantsLoaded = true;
        parentDir.childrenLoaded = true;
    }

    private DefaultMutableTreeNode getLoadedNode(final DefaultMutableTreeNode parentNode, final String filePath) {
        Dir dir;
        for (int j = 0; j < parentNode.getChildCount(); j++) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentNode.getChildAt(j);
            dir = (Dir) node.getUserObject();
            if (dir.getPath().equals(filePath)) {
                return node;
            }
        }
        return null;
    }

    File[] getSelections() {

        File[] files = new File[0];

        // All selected nodes need to be expanded since we are using lazy loading.
        TreePath selectedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();
        selectedPaths = checkTreeManager.getSelectionModel().getSelectionPaths();
        if (selectedPaths != null) {
            files = new File[selectedPaths.length];
            for (int i = 0; i < selectedPaths.length; i++) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent();
                files[i] = (File) node.getUserObject();
            }
            return files;
        }
        // Return files in the order that the user clicked them
        return files;
    }

    /**
     * Expands a given node in a JTree.
     * 
     * @param tree
     *            The JTree to expand.
     * @param model
     *            The TreeModel for tree.
     * @param node
     *            The node within tree to expand.
     * @param row
     *            The displayed row in tree that represents node.
     * @param depth
     *            The depth to which the tree should be expanded. Zero will just expand node, a negative value will fully expand the tree, and a positive value will recursively expand the tree to that depth relative to node.
     */
    private static int expandJTreeNode(final javax.swing.JTree tree, final javax.swing.tree.TreeModel model, final Object node, int row, final int depth) {
        if (node != null && !model.isLeaf(node)) {
            tree.expandRow(row);
            if (depth != 0) {
                for (int index = 0; row + 1 < tree.getRowCount() && index < model.getChildCount(node); index++) {
                    row++;
                    final Object child = model.getChild(node, index);
                    if (child == null) {
                      break;
                    }
                    javax.swing.tree.TreePath path;
                    while ((path = tree.getPathForRow(row)) != null && path.getLastPathComponent() != child) {
                        row++;
                    }
                    if (path == null) {
                        break;
                    }
                    row = expandJTreeNode(tree, model, child, row, depth - 1);
                }
            }
        }
        return row;
    } // expandJTreeNode()}

    int getSelectionCount() {
        final TreePath[] selections = checkTreeManager.getSelectionModel().getSelectionPaths();
        if (selections == null) {
            return 0;
        }
        return selections.length;
    }

    private List<String> setInitialSelections(final CheckTreeSelectionModelB checkTreeSelectionModel, final List<String> initialPaths) {
        if (initialPaths == null) {
            return null;
        }
        final DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
        final List<String> pathsNotFound = new ArrayList<String>();
        for (final String initialFilePath : initialPaths) {
            boolean found = false;
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                if (processChild(checkTreeSelectionModel, initialFilePath, childNode)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                pathsNotFound.add(initialFilePath);
            }
        }
        return pathsNotFound;
    }

    private boolean processChild(final CheckTreeSelectionModelB checkTreeSelectionModel, final String initialFilePath, final DefaultMutableTreeNode currentNode) {
        final String currentFilePath = ((File) currentNode.getUserObject()).getPath();
        if (initialFilePath.equals(currentFilePath)) {
            // this is a match
            checkTreeSelectionModel.addSelectionPaths(new TreePath[] { new TreePath(currentNode.getPath()) });
            return true;
        }
        if (initialFilePath.startsWith(currentFilePath)) {
            // Expand tree and it will load the children lazily
            tree.expandPath(new TreePath(currentNode.getPath()));
            for (int i = 0; i < currentNode.getChildCount(); i++) {
                final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) currentNode.getChildAt(i);
                if (processChild(checkTreeSelectionModel, initialFilePath, childNode)) {
                    return true;
                }
            }
        }
        else {
            return false;
        }
        return false;
    }

    public void setRecurseSelection(final boolean recurseSelection) {
        this.recurseSelection = recurseSelection;
    }

    /**
     * Returns all paths that are descendants to given path
     * 
     * @param path
     * @param pathList
     * @return
     */
    List<TreePath> getDescendantPaths(final TreePath path, final List<TreePath> pathList) {
        final Object node = path.getLastPathComponent();
        if (tree.getModel().isLeaf(node)) {
            return pathList;
        }
        final int num = tree.getModel().getChildCount(node);
        for (int iChild = 0; iChild < num; iChild++) {
            final TreePath childPath = path.pathByAddingChild(tree.getModel().getChild(node, iChild));
            pathList.add(childPath);
            getDescendantPaths(childPath, pathList);
        }
        return pathList;
    }

    private static final long serialVersionUID = 1L;
}