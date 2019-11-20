/*
 *
 *  *  This file is part of Empty3.
 *  *
 *  *     Empty3 is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation, either version 2 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     Empty3 is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU General Public License
 *  *     along with Empty3.  If not, see <https://www.gnu.org/licenses/>. 2
 *
 *
 */

package one.empty3.gui;

import javax.swing.border.*;
import net.miginfocom.swing.MigLayout;
import one.empty3.library.*;
import one.empty3.library.core.script.ExtensionFichierIncorrecteException;
import one.empty3.library.core.script.Loader;
import one.empty3.library.core.script.VersionNonSupporteeException;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
/*
 * Created by JFormDesigner on Thu Jun 27 11:40:57 CEST 2019
 */


/**
 * @author Manuel Dahmen
 */
public class Main implements PropertyChangeListener {
    private boolean drawingPointCoords;
    private Point click;
    private String toDraw;
    private boolean running = true;
    private boolean refreshXMLactioned;
    private ThreadGraphicalEditor threadGraphicalEditor;
    private boolean selectAndRotate = false;
    private boolean translateR;
    private boolean rotateR;
    private int graphicEditROTATE = 0;
    private int graphicEditMOVE = 1;
    private int graphicEditROTATER = 2;
    private int graphicEditTRANSLATER = 3;
    private int graphicEdit = -1;
    private GraphicalEdit2 graphicalEdit2;

    public static void main(String [] args)
    {
        Main main = new Main();
    }

    public Main() {
        dataModel = new DataModel();
        initComponents();
        getUpdateView().setFF(this);
        getTextureEditor().setMain(this);
        getEditor().addPropertyChangeListener(this);
        getUpdateView().getzRunner().addPropertyChangeListener(this);
        ThreadDrawingCoords threadDrawingCoords = new ThreadDrawingCoords();
        threadDrawingCoords.start();
        getLoadSave1().setMain(this);

        JDialog licence = new JDialog(MainWindow, "Licence");
        JTextArea area = null;
        licence.add(area=new JTextArea("Création d'objets simples\n" +
                "    Copyright (C) 2019  Manuel Dahmen\n" +
                "\n" +
                "    This program is free software: you can redistribute it and/or modify\n" +
                "    it under the terms of the GNU General Public License as published by\n" +
                "    the Free Software Foundation, either version 3 of the License, or\n" +
                "    (at your option) any later version.\n" +
                "\n" +
                "    This program is distributed in the hope that it will be useful,\n" +
                "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "    GNU General Public License for more details.\n" +
                "\n" +
                "    You should have received a copy of the GNU General Public License\n" +
                "    along with this program.  If not, see <https://www.gnu.org/licenses/>."));
        licence.pack();
        area.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                licence.setVisible(false);
                licence.dispose();
            }
        });
        licence.setVisible(true);
        running = true;
        treeSelIn.setModel(new TreeModelSelection());
        treeSelOut.setModel(new TreeModelSelection());
    }

    private TextureEditor getTextureEditor() {
        return textureEditor1;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public UpdateViewMain getUpdateView() {
        return updateViewMain;
    }

    public void setUpdateView(UpdateViewMain updateView) {
        this.updateViewMain = updateView;
    }

    private DataModel dataModel;


    private void menuItemNewActionPerformed(ActionEvent e) {
        dataModel = new DataModel();
        dataModel.setScene(new Scene());
    }

    private void ScriptPanelPropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
    }
/*
    private void menuItemLoadActionPerformed(ActionEvent e) {
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setDialogTitle("Open file");
        jFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        jFileChooser.showDialog(MainWindow, "Load");
        File selectedFile = jFileChooser.getSelectedFile();
        Scene scene = new Scene();
        try {
            new Loader().load(selectedFile, scene);
            setDataModel(new DataModel(selectedFile));
        } catch (VersionNonSupporteeException e1) {
            e1.printStackTrace();
        } catch (ExtensionFichierIncorrecteException e1) {
            e1.printStackTrace();
        }
    }
*/
    private void menuItemSaveActionPerformed(ActionEvent e) {
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setDialogTitle("Save file");
        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser.showDialog(MainWindow, "Save");
        File selectedFile = jFileChooser.getSelectedFile();
        Scene scene = new Scene();
        try {
            new Loader().saveTxt(selectedFile, scene);
            dataModel = new DataModel();
            dataModel.setScene(scene);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void editorPropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
    }

    private void MainWindowKeyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
        {
            getUpdateView().getzRunner().setStopCurrentRender(true);
        }
    }

    private void updateViewMouseClicked(MouseEvent e) {
            try {
                System.out.println(e.getX());
                System.out.println(e.getY());
                Point3D point3D = getUpdateView().getzRunner().getzBuffer().clickAt(
                        (int)e.getX(), (int)e.getY());

                if(point3D==null && point3D.equals(ZBufferImpl.INFINI)) {
                    toDraw = "background(texture)";
                }else{
            String[] ps = new String[3];
            for (int i = 0; i < 3; i++)
                ps[i] = String.format("%.3f", point3D.get(i));
            toDraw = "p3(" + ps[0] + ", " + ps[1] + ", " + ps[2] + ")";
        }
                click = e.getPoint();
                drawingPointCoords = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

    }

    public LoadSave getLoadSave1() {
        return loadSave1;
    }

    private void buttonSaveRActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Thread()
        {

            @Override
            public void run() {
                super.run();
                Integer imageWidth;
                Integer imageHeight;
                try {
                    imageWidth = Integer.parseInt(textFieldXres.getText());
                    imageHeight = Integer.parseInt(textFieldYres.getText());
                }catch (NumberFormatException ex)
                {
                    imageWidth = -1;
                    imageHeight = -1;
                    Logger.getAnonymousLogger().info("Invalid numbers (must be int>0 => default");
                }
                if(imageHeight<=0 || imageWidth<=0)
                {
                    imageWidth = 1920;
                    imageHeight = 1080;

                }
                ZBufferImpl zBuffer = new ZBufferImpl(imageWidth, imageHeight);
                zBuffer.scene(getDataModel().getScene());
                zBuffer.setDisplayType(updateViewMain.getView().getzDiplayType());
                zBuffer.draw();
                ECBufferedImage image = zBuffer.image();
                try {
                    File file = new File(getDataModel().getNewImageFile());
                    ImageIO.write(image, "jpg", file);
                    Logger.getAnonymousLogger().info("Image rendered and saved as <a href="+file.getCanonicalPath()+"\">"+file.getCanonicalPath()+"</a>");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    private void checkBoxBindToPreviewActionPerformed(ActionEvent e) {
        if(checkBoxBindToPreview.isSelected())
        {
            bindingGroup.bind();
        } else
        {
            bindingGroup.unbind();
        }
    }

    private void buttonRenderActionPerformed(ActionEvent e) {
        Logger.getAnonymousLogger().info("Nothing here");
    }

    private void tabbedPaneXMLComponentAdded(ContainerEvent e) {
        new Thread() {
            @Override
            public void run() {
                while (isRunning()) {
                    if (isRefreshXMLactioned())
                        try {
                            Scene scene = getDataModel().getScene();
                            StringBuilder stringBuilder = new StringBuilder();
                            scene.xmlRepresentation(getDataModel().getDirectory(false), scene, stringBuilder);

                            textAreaXML.setText(stringBuilder.toString());

                            setRefreshXMLactioned(false);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public boolean isRunning() {
        return running;
    }

    private void buttonRefreshXMLActionPerformed(ActionEvent e) {
        setRefreshXMLactioned(true);
    }

    private void setRefreshXMLactioned(boolean value) {
        this.refreshXMLactioned = value;
    }

    public boolean isRefreshXMLactioned() {
        return refreshXMLactioned;
    }

    private void checkBoxActiveItemStateChanged(ItemEvent e) {
        if(e.getStateChange()== ItemEvent.SELECTED)
        {
            System.out.println("Graphical edition enabled");
            getUpdateView().getzRunner().setGraphicalEditing(true);
            threadGraphicalEditor = new ThreadGraphicalEditor(this);
            threadGraphicalEditor.start();
        }
        else if(e.getStateChange()== ItemEvent.DESELECTED)
        {
            System.out.println("Graphical edition disabled");
            threadGraphicalEditor.stop();
            getUpdateView().getzRunner().setGraphicalEditing(false);
        }
    }

    private void buttonXMLActionPerformed(ActionEvent e) {
        new Thread() {
            @Override
            public void run() {
                while (isRunning()) {
                    if (isRefreshXMLactioned())
                        try {
                            Scene scene = getDataModel().getScene();
                            StringBuilder stringBuilder = new StringBuilder();
                            scene.xmlRepresentation(getDataModel().getDirectory(false),(Representable) scene, stringBuilder);

                            textAreaXML.setText(stringBuilder.toString());

                            setRefreshXMLactioned(false);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void updateViewMouseDragged(MouseEvent e) {
        // TODO add your code here
    }

    private void updateViewMouseMoved(MouseEvent e) {
        // TODO add your code here
    }

    private void updateViewMouseWheelMoved(MouseWheelEvent e) {
        // TODO add your code here
    }

    public boolean isSelectAndRotate() {
        return getUpdateView().getzRunner().getSelRot();
    }

    private void checkBoxSelRotActionPerformed(ActionEvent e) {
       getUpdateView().getzRunner().setSelRot(((JRadioButton)e.getSource()).isSelected());
        graphicalEdit2.setSelTypeIn(GraphicalEdit2.SelType.SelectRotate);
    }

    private void checkBoxActiveActionPerformed(ActionEvent e) {
        graphicalEdit2 = new GraphicalEdit2();
        graphicalEdit2.setMain(this);

    }

    private void radioButton1ActionPerformed(ActionEvent e) {
        if(graphicalEdit2!=null) {
            graphicalEdit2.setRunning(false);
            graphicalEdit2 = null;
        }

    }

    public GraphicalEdit2 getGraphicalEdit2() {
        return graphicalEdit2;
    }

    public void setGraphicalEdit2(GraphicalEdit2 graphicalEdit2) {
        this.graphicalEdit2 = graphicalEdit2;
    }

    private void checkBoxSelMultipleObjectsActionPerformed(ActionEvent e) {
        graphicalEdit2.setStartSel1(true);
        graphicalEdit2.setSelectingMultipleObjectsIn(((JCheckBox)e.getSource()).isSelected());

    }

    private void checkBoxSelMultiplePointsActionPerformed(ActionEvent e) {
        graphicalEdit2.setStartSel1(true);
        graphicalEdit2.setSelectArbitraryPointsIn(((JCheckBox)e.getSource()).isSelected());
    }

    private void checkBoxSelArbitraryPointsActionPerformed(ActionEvent e) {
        graphicalEdit2.setSelectArbitraryPointsIn(((JCheckBox)e.getSource()).isSelected());
    }

    private void radioButtonTranslateActionPerformed(ActionEvent e) {
        graphicalEdit2.setSelTypeOut(((JRadioButton)e.getSource()).isSelected()==true?GraphicalEdit2.SelType.Translate:null);
    }

    private void radioButtonRotateActionPerformed(ActionEvent e) {
        graphicalEdit2.setSelTypeOut(((JRadioButton)e.getSource()).isSelected()==true?GraphicalEdit2.SelType.Rotate:null);
    }

    private void checkBoxEndSelActionPerformed(ActionEvent e) {
        graphicalEdit2.setEndSel1(((JCheckBox)e.getSource()).isSelected());
    }

    private void buttonDuplicateOnPointsActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton)e.getSource()).isSelected()==true?GraphicalEdit2.Action.duplicateOnPoints:null);
    }

    private void buttonDuplicateOnCurveActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton)e.getSource()).isSelected()==true?GraphicalEdit2.Action.duplicateOnCurve:null);
    }

    private void buttonDuplicateOnSurfaceActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton)e.getSource()).isSelected()==true?GraphicalEdit2.Action.duplicateOnSurface:null);
    }

    private void buttonExtrudeSelActionPerformed(ActionEvent e) {
        graphicalEdit2.setActionToPerform(((JRadioButton)e.getSource()).isSelected()==true?GraphicalEdit2.Action.extrude:null);
    }

    private void buttonSelGoActionPerformed(ActionEvent e) {
        graphicalEdit2.performAction();
    }

    private void radioButtonSel1ActionPerformed(ActionEvent e) {
        graphicalEdit2.setActiveSelection(0);
    }

    private void radioButtonSel2ActionPerformed(ActionEvent e) {
        graphicalEdit2.setActiveSelection(1);
    }




    class ThreadDrawingCoords  extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    /*if(drawingPointCoords) {
                        Graphics graphics = getUpdateView().getzRunner().getLastImage().getGraphics();
                        graphics.setColor(Color.BLACK);
                        graphics.drawBytes(toDraw.getBytes(), 0, toDraw.getBytes().length, click.x, click.y);
                    }*/
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        this.MainWindow = new JFrame();
        this.panel6 = new JPanel();
        this.menu1 = new JMenu();
        this.menuItemNew = new JMenuItem();
        this.menuItemLoad = new JMenuItem();
        this.menuItemSave = new JMenuItem();
        this.panel5 = new JSplitPane();
        this.panel4 = new JSplitPane();
        this.editor = new REditor(this, dataModel.getScene());
        this.updateViewMain = new UpdateViewMain();
        this.tabbedPane1 = new JTabbedPane();
        this.textureEditor1 = new TextureEditor();
        this.panel1 = new JPanel();
        this.objectEditorBase1 = new ObjectEditorBase();
        this.panel2 = new JPanel();
        this.labelX = new JLabel();
        this.textFieldXres = new JTextField();
        this.checkBoxBindToPreview = new JCheckBox();
        this.labelY = new JLabel();
        this.textFieldYres = new JTextField();
        this.label3 = new JLabel();
        this.comboBox1 = new JComboBox<>();
        this.buttonRender = new JButton();
        this.buttonSaveR = new JButton();
        this.panel3 = new JPanel();
        this.loadSave1 = new LoadSave();
        this.panel7 = new JPanel();
        this.buttonXML = new JButton();
        this.buttonRefreshXML = new JButton();
        this.scrollPane1 = new JScrollPane();
        this.textAreaXML = new JTextArea();
        this.panel8 = new JPanel();
        this.checkBoxActive = new JRadioButton();
        this.label1 = new JLabel();
        this.radioButtonSel1 = new JRadioButton();
        this.scrollPane2 = new JScrollPane();
        this.treeSelIn = new JTree();
        this.label2 = new JLabel();
        this.checkBoxSelRot = new JRadioButton();
        this.checkBoxSelMultipleObjects = new JCheckBox();
        this.radioButtonSel2 = new JRadioButton();
        this.buttonDuplicateOnPoints = new JRadioButton();
        this.radioButton1 = new JRadioButton();
        this.checkBoxSelMultiplePoints = new JCheckBox();
        this.buttonDuplicateOnCurve = new JRadioButton();
        this.checkBoxSelArbitraryPoints = new JCheckBox();
        this.buttonDuplicateOnSurface = new JRadioButton();
        this.radioButtonTranslate = new JRadioButton();
        this.scrollPane3 = new JScrollPane();
        this.treeSelOut = new JTree();
        this.buttonExtrudeSel = new JRadioButton();
        this.radioButtonRotate = new JRadioButton();
        this.checkBoxEndSel = new JCheckBox();
        this.button1 = new JButton();

        //======== MainWindow ========
        {
            this.MainWindow.setTitle("Empty3");
            this.MainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.MainWindow.setBackground(new Color(204, 255, 255));
            this.MainWindow.setVisible(true);
            this.MainWindow.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            this.MainWindow.setMinimumSize(new Dimension(1000, 400));
            this.MainWindow.setIconImage(new ImageIcon(getClass().getResource("/one/empty3/library/mite.png")).getImage());
            this.MainWindow.addPropertyChangeListener("fieldFunctionsPropertyChanged", e -> ScriptPanelPropertyChange(e));
            this.MainWindow.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    MainWindowKeyPressed(e);
                }
            });
            Container MainWindowContentPane = this.MainWindow.getContentPane();
            MainWindowContentPane.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]"));

            //======== panel6 ========
            {
                this.panel6.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[]"));

                //======== menu1 ========
                {
                    this.menu1.setText("File");

                    //---- menuItemNew ----
                    this.menuItemNew.setText("New");
                    this.menuItemNew.addActionListener(e -> {
			menuItemNewActionPerformed(e);
			menuItemNewActionPerformed(e);
		});
                    this.menu1.add(this.menuItemNew);

                    //---- menuItemLoad ----
                    this.menuItemLoad.setText("Load");
                    this.menu1.add(this.menuItemLoad);

                    //---- menuItemSave ----
                    this.menuItemSave.setText("Save");
                    this.menuItemSave.addActionListener(e -> menuItemSaveActionPerformed(e));
                    this.menu1.add(this.menuItemSave);
                }
                this.panel6.add(this.menu1, "pad 5,cell 0 0 1 3,aligny top,growy 0,wmin 100,hmin 20");

                //======== panel5 ========
                {
                    this.panel5.setOrientation(JSplitPane.VERTICAL_SPLIT);

                    //======== panel4 ========
                    {

                        //---- editor ----
                        this.editor.addPropertyChangeListener(e -> editorPropertyChange(e));
                        this.panel4.setLeftComponent(this.editor);

                        //---- updateViewMain ----
                        this.updateViewMain.setBackground(new Color(204, 255, 204));
                        this.updateViewMain.setMinimumSize(new Dimension(400, 400));
                        this.updateViewMain.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                updateViewMouseClicked(e);
                                updateViewMouseClicked(e);
                            }
                        });
                        this.updateViewMain.addMouseMotionListener(new MouseMotionAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                updateViewMouseDragged(e);
                            }
                            @Override
                            public void mouseMoved(MouseEvent e) {
                                updateViewMouseMoved(e);
                            }
                        });
                        this.updateViewMain.addMouseWheelListener(e -> updateViewMouseWheelMoved(e));
                        this.panel4.setRightComponent(this.updateViewMain);
                    }
                    this.panel5.setTopComponent(this.panel4);

                    //======== tabbedPane1 ========
                    {
                        this.tabbedPane1.setMinimumSize(new Dimension(400, 300));
                        this.tabbedPane1.addContainerListener(new ContainerAdapter() {
                            @Override
                            public void componentAdded(ContainerEvent e) {
                                tabbedPaneXMLComponentAdded(e);
                            }
                        });

                        //---- textureEditor1 ----
                        this.textureEditor1.setMinimumSize(new Dimension(400, 300));
                        this.tabbedPane1.addTab("Textures", this.textureEditor1);

                        //======== panel1 ========
                        {
                            this.panel1.setLayout(new MigLayout(
                                "hidemode 3",
                                // columns
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]",
                                // rows
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]"));
                            this.panel1.add(this.objectEditorBase1, "cell 0 0 6 6,dock center");
                        }
                        this.tabbedPane1.addTab("Position", this.panel1);

                        //======== panel2 ========
                        {
                            this.panel2.setLayout(new MigLayout(
                                "hidemode 3",
                                // columns
                                "[fill]" +
                                "[fill]" +
                                "[fill]",
                                // rows
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]"));

                            //---- labelX ----
                            this.labelX.setText("res.x");
                            this.panel2.add(this.labelX, "cell 0 0");
                            this.panel2.add(this.textFieldXres, "cell 1 0");

                            //---- checkBoxBindToPreview ----
                            this.checkBoxBindToPreview.setText("Binds to preview");
                            this.checkBoxBindToPreview.setSelected(true);
                            this.checkBoxBindToPreview.addActionListener(e -> checkBoxBindToPreviewActionPerformed(e));
                            this.panel2.add(this.checkBoxBindToPreview, "cell 2 0");

                            //---- labelY ----
                            this.labelY.setText("res.y");
                            this.panel2.add(this.labelY, "cell 0 1");
                            this.panel2.add(this.textFieldYres, "cell 1 1");

                            //---- label3 ----
                            this.label3.setText("Rendering type");
                            this.panel2.add(this.label3, "cell 0 2");

                            //---- comboBox1 ----
                            this.comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
                                "DISPLAY_ALL",
                                "SURFACE_DISPLAY_TEXT_QUADS",
                                "SURFACE_DISPLAY_TEXT_TRI",
                                "SURFACE_DISPLAY_COL_QUADS",
                                "SURFACE_DISPLAY_COL_TRI",
                                "SURFACE_DISPLAY_LINES",
                                "SURFACE_DISPLAY_POINTS"
                            }));
                            this.panel2.add(this.comboBox1, "cell 1 2");

                            //---- buttonRender ----
                            this.buttonRender.setText("Render");
                            this.buttonRender.addActionListener(e -> buttonRenderActionPerformed(e));
                            this.panel2.add(this.buttonRender, "cell 0 3");

                            //---- buttonSaveR ----
                            this.buttonSaveR.setText("Save");
                            this.buttonSaveR.addActionListener(e -> buttonSaveRActionPerformed(e));
                            this.panel2.add(this.buttonSaveR, "cell 1 3");
                        }
                        this.tabbedPane1.addTab("Rendu", this.panel2);

                        //======== panel3 ========
                        {
                            this.panel3.setLayout(new MigLayout(
                                "hidemode 3",
                                // columns
                                "[fill]" +
                                "[fill]",
                                // rows
                                "[]" +
                                "[]" +
                                "[]"));
                            this.panel3.add(this.loadSave1, "cell 0 0 2 3,dock center");
                        }
                        this.tabbedPane1.addTab("LOad/save/export", this.panel3);

                        //======== panel7 ========
                        {
                            this.panel7.setLayout(new MigLayout(
                                "hidemode 3",
                                // columns
                                "[fill]" +
                                "[fill]" +
                                "[fill]",
                                // rows
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]"));

                            //---- buttonXML ----
                            this.buttonXML.setText("Open");
                            this.buttonXML.addActionListener(e -> {
			buttonXMLActionPerformed(e);
			buttonXMLActionPerformed(e);
		});
                            this.panel7.add(this.buttonXML, "cell 0 0");

                            //---- buttonRefreshXML ----
                            this.buttonRefreshXML.setText("Refresh");
                            this.buttonRefreshXML.addActionListener(e -> buttonRefreshXMLActionPerformed(e));
                            this.panel7.add(this.buttonRefreshXML, "cell 0 0");

                            //======== scrollPane1 ========
                            {
                                this.scrollPane1.setViewportView(this.textAreaXML);
                            }
                            this.panel7.add(this.scrollPane1, "cell 1 0,dock center");
                        }
                        this.tabbedPane1.addTab("XML", this.panel7);

                        //======== panel8 ========
                        {
                            this.panel8.setLayout(new MigLayout(
                                "hidemode 3",
                                // columns
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]",
                                // rows
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]" +
                                "[]"));

                            //---- checkBoxActive ----
                            this.checkBoxActive.setText("Active graphical editing");
                            this.checkBoxActive.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                            this.checkBoxActive.setBorderPainted(true);
                            this.checkBoxActive.addItemListener(e -> checkBoxActiveItemStateChanged(e));
                            this.checkBoxActive.addActionListener(e -> checkBoxActiveActionPerformed(e));
                            this.panel8.add(this.checkBoxActive, "cell 0 0");

                            //---- label1 ----
                            this.label1.setText("Modify Representaable");
                            this.panel8.add(this.label1, "cell 2 0");

                            //---- radioButtonSel1 ----
                            this.radioButtonSel1.setText("Selection 1 (IN)");
                            this.radioButtonSel1.addActionListener(e -> radioButtonSel1ActionPerformed(e));
                            this.panel8.add(this.radioButtonSel1, "cell 3 0");

                            //======== scrollPane2 ========
                            {
                                this.scrollPane2.setViewportView(this.treeSelIn);
                            }
                            this.panel8.add(this.scrollPane2, "cell 4 0 1 4");

                            //---- label2 ----
                            this.label2.setText("Duplicate");
                            this.panel8.add(this.label2, "cell 6 0");

                            //---- checkBoxSelRot ----
                            this.checkBoxSelRot.setText("Select and rotate");
                            this.checkBoxSelRot.addActionListener(e -> {
			checkBoxSelRotActionPerformed(e);
			checkBoxSelRotActionPerformed(e);
		});
                            this.panel8.add(this.checkBoxSelRot, "cell 0 1");

                            //---- checkBoxSelMultipleObjects ----
                            this.checkBoxSelMultipleObjects.setText("Selection Multiple objects");
                            this.checkBoxSelMultipleObjects.addActionListener(e -> {
			checkBoxSelMultipleObjectsActionPerformed(e);
			checkBoxSelMultipleObjectsActionPerformed(e);
		});
                            this.panel8.add(this.checkBoxSelMultipleObjects, "cell 2 1");

                            //---- radioButtonSel2 ----
                            this.radioButtonSel2.setText("Selection 2 (OUT)");
                            this.radioButtonSel2.addActionListener(e -> radioButtonSel2ActionPerformed(e));
                            this.panel8.add(this.radioButtonSel2, "cell 3 1");

                            //---- buttonDuplicateOnPoints ----
                            this.buttonDuplicateOnPoints.setText("Duplicate object on points");
                            this.buttonDuplicateOnPoints.addActionListener(e -> buttonDuplicateOnPointsActionPerformed(e));
                            this.panel8.add(this.buttonDuplicateOnPoints, "cell 6 1");

                            //---- radioButton1 ----
                            this.radioButton1.setText("Disactivate");
                            this.radioButton1.addActionListener(e -> radioButton1ActionPerformed(e));
                            this.panel8.add(this.radioButton1, "cell 0 2");

                            //---- checkBoxSelMultiplePoints ----
                            this.checkBoxSelMultiplePoints.setText("Select multiple");
                            this.checkBoxSelMultiplePoints.setActionCommand("Select multiple points");
                            this.checkBoxSelMultiplePoints.addActionListener(e -> checkBoxSelMultiplePointsActionPerformed(e));
                            this.panel8.add(this.checkBoxSelMultiplePoints, "cell 2 2");

                            //---- buttonDuplicateOnCurve ----
                            this.buttonDuplicateOnCurve.setText("on curve");
                            this.buttonDuplicateOnCurve.addActionListener(e -> buttonDuplicateOnCurveActionPerformed(e));
                            this.panel8.add(this.buttonDuplicateOnCurve, "cell 6 2");

                            //---- checkBoxSelArbitraryPoints ----
                            this.checkBoxSelArbitraryPoints.setText("Select arb points");
                            this.checkBoxSelArbitraryPoints.addActionListener(e -> checkBoxSelArbitraryPointsActionPerformed(e));
                            this.panel8.add(this.checkBoxSelArbitraryPoints, "cell 2 3");

                            //---- buttonDuplicateOnSurface ----
                            this.buttonDuplicateOnSurface.setText("on surface");
                            this.buttonDuplicateOnSurface.addActionListener(e -> buttonDuplicateOnSurfaceActionPerformed(e));
                            this.panel8.add(this.buttonDuplicateOnSurface, "cell 6 3");

                            //---- radioButtonTranslate ----
                            this.radioButtonTranslate.setText("Translate");
                            this.radioButtonTranslate.addActionListener(e -> radioButtonTranslateActionPerformed(e));
                            this.panel8.add(this.radioButtonTranslate, "cell 2 4");

                            //======== scrollPane3 ========
                            {
                                this.scrollPane3.setViewportView(this.treeSelOut);
                            }
                            this.panel8.add(this.scrollPane3, "cell 4 4 1 3");

                            //---- buttonExtrudeSel ----
                            this.buttonExtrudeSel.setText("Extrude selection");
                            this.buttonExtrudeSel.addActionListener(e -> buttonExtrudeSelActionPerformed(e));
                            this.panel8.add(this.buttonExtrudeSel, "cell 6 4");

                            //---- radioButtonRotate ----
                            this.radioButtonRotate.setText("Rotate");
                            this.radioButtonRotate.addActionListener(e -> radioButtonRotateActionPerformed(e));
                            this.panel8.add(this.radioButtonRotate, "cell 2 5");

                            //---- checkBoxEndSel ----
                            this.checkBoxEndSel.setText("End selection");
                            this.checkBoxEndSel.addActionListener(e -> checkBoxEndSelActionPerformed(e));
                            this.panel8.add(this.checkBoxEndSel, "cell 2 6");

                            //---- button1 ----
                            this.button1.setText("GO");
                            this.button1.addActionListener(e -> buttonSelGoActionPerformed(e));
                            this.panel8.add(this.button1, "cell 6 6");
                        }
                        this.tabbedPane1.addTab("graphical editing", this.panel8);
                    }
                    this.panel5.setBottomComponent(this.tabbedPane1);
                }
                this.panel6.add(this.panel5, "cell 0 2,dock center");
            }
            MainWindowContentPane.add(this.panel6, "cell 0 0,dock center");
            this.MainWindow.setSize(1620, 1085);
            this.MainWindow.setLocationRelativeTo(this.MainWindow.getOwner());
        }

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(this.checkBoxActive);
        buttonGroup1.add(this.checkBoxSelRot);
        buttonGroup1.add(this.radioButton1);
        buttonGroup1.add(this.radioButtonTranslate);
        buttonGroup1.add(this.radioButtonRotate);

        //---- buttonGroup3 ----
        ButtonGroup buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(this.radioButtonSel1);
        buttonGroup3.add(this.radioButtonSel2);

        //---- buttonGroup2 ----
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(this.buttonDuplicateOnPoints);
        buttonGroup2.add(this.buttonDuplicateOnCurve);
        buttonGroup2.add(this.buttonDuplicateOnSurface);
        buttonGroup2.add(this.buttonExtrudeSel);

        //---- bindings ----
        this.bindingGroup = new BindingGroup();
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            this.updateViewMain, BeanProperty.create("width"),
            this.textFieldXres, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST")));
        this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            this.updateViewMain, BeanProperty.create("height"),
            this.textFieldYres, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST")));
        {
            Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ,
                this.comboBox1, BeanProperty.create("selectedIndex"),
                this.updateViewMain, BeanProperty.create("view.zDiplayType"));
            binding.setSourceNullValue(0);
            binding.setSourceUnreadableValue(0);
            binding.setTargetNullValue(0);
            this.bindingGroup.addBinding(binding);
            binding.bind();
        }
        this.bindingGroup.bind();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JFrame MainWindow;
    private JPanel panel6;
    private JMenu menu1;
    private JMenuItem menuItemNew;
    private JMenuItem menuItemLoad;
    private JMenuItem menuItemSave;
    private JSplitPane panel5;
    private JSplitPane panel4;
    private REditor editor;
    private UpdateViewMain updateViewMain;
    private JTabbedPane tabbedPane1;
    private TextureEditor textureEditor1;
    private JPanel panel1;
    private ObjectEditorBase objectEditorBase1;
    private JPanel panel2;
    private JLabel labelX;
    private JTextField textFieldXres;
    private JCheckBox checkBoxBindToPreview;
    private JLabel labelY;
    private JTextField textFieldYres;
    private JLabel label3;
    private JComboBox<String> comboBox1;
    private JButton buttonRender;
    private JButton buttonSaveR;
    private JPanel panel3;
    private LoadSave loadSave1;
    private JPanel panel7;
    private JButton buttonXML;
    private JButton buttonRefreshXML;
    private JScrollPane scrollPane1;
    private JTextArea textAreaXML;
    private JPanel panel8;
    private JRadioButton checkBoxActive;
    private JLabel label1;
    private JRadioButton radioButtonSel1;
    private JScrollPane scrollPane2;
    private JTree treeSelIn;
    private JLabel label2;
    private JRadioButton checkBoxSelRot;
    private JCheckBox checkBoxSelMultipleObjects;
    private JRadioButton radioButtonSel2;
    private JRadioButton buttonDuplicateOnPoints;
    private JRadioButton radioButton1;
    private JCheckBox checkBoxSelMultiplePoints;
    private JRadioButton buttonDuplicateOnCurve;
    private JCheckBox checkBoxSelArbitraryPoints;
    private JRadioButton buttonDuplicateOnSurface;
    private JRadioButton radioButtonTranslate;
    private JScrollPane scrollPane3;
    private JTree treeSelOut;
    private JRadioButton buttonExtrudeSel;
    private JRadioButton radioButtonRotate;
    private JCheckBox checkBoxEndSel;
    private JButton button1;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public REditor getEditor() {
        return editor;
    }

    public void setEditor(REditor editor) {
        this.editor = editor;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //Logger.getAnonymousLogger().info("Property main changed= "+evt.getPropertyName());
        if (evt.getPropertyName().equals("representable")) {
            Logger.getAnonymousLogger().info("representable changed");
            RepresentableEditor[] representableEditors = {getEditor(), getUpdateView(), getPositionEditor()};
            for (RepresentableEditor representableEditor : representableEditors) {
                if (!evt.getSource().equals(representableEditor) && evt.getNewValue() instanceof Representable) {
                    representableEditor.initValues((Representable) evt.getNewValue());
                    Logger.getAnonymousLogger().info(representableEditor.getClass().getName()+".initValue()");
                }
            }
        } else if (evt.getPropertyName().equals("renderedImageOK")) {
            if (evt.getNewValue()==null) {
            } else {
                if(evt.getNewValue().equals(-1))
                {
                    try {
                        this.getUpdateView().getzRunner().setLastImage(ImageIO.read(new File("resources/img/RESULT_FAILURE.JPG")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(evt.getNewValue().equals(0))
                {
                    try {
                        this.getUpdateView().getzRunner().setLastImage(ImageIO.read(new File("resources/img/WAIT.JPG")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public RepresentableEditor getPositionEditor() {
        return objectEditorBase1;
    }

    public boolean isTranslateR()
    {
        return translateR;
    }
    public boolean isRotateR()
    {
        return rotateR;
    }


}
