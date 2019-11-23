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

import one.empty3.library.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by manue on 26-06-19.
 */
public class UpdateViewMain extends JPanel implements RepresentableEditor {
    private Main main;
    private Scene scene;
    private Representable currentRepresentable;


    public UpdateViewMain() {
        setView(new FunctionView());
        setzRunner(new ZRunnerMain());
        addMouseListener(new MouseAdapter() {
            public Representable representable;
            public ThreadDrawing threadDrawing =null;
            public Point3D mousePoint3Dorig;
            Point3D mousePoint3D;
            Point mousePoint = null;
            ArcBall2 arcBall;

            class ThreadDrawing extends Thread {
                boolean running;
                private boolean pause = false;

                public void run() {
                    running = true;
                         while (isRunning()) {
                             while (isPause()) {
                                 try {
                                     Thread.sleep(100);
                                 } catch (InterruptedException e) {
                                     e.printStackTrace();
                                 }
                             }
                            Point location = MouseInfo.getPointerInfo().getLocation();
                            SwingUtilities.convertPointFromScreen(location, main.getUpdateView());
                            mousePoint = location;
                            try {
                                if (main.isSelectAndRotate()) {
                                    arcBall.moveTo((int) mousePoint.getX(), (int) mousePoint.getY());
                                    getzRunner().afterDraw(new Thread() {
                                        @Override
                                        public void run() {
                                            //getzRunner().getzBuffer().draw(new Sphere(mousePoint3D, 10.0));
                                        }
                                    });
                                    ////System.out.println("Mouse rotation moved");
                                } else {
                                    //drawPoint(mousePoint);

                                }

                            } catch (ArrayIndexOutOfBoundsException ex) {
                                ex.printStackTrace();
                            }
                        }

                }

                private boolean isRunning() {
                    return running;
                }

                public void setRunning(boolean running) {
                    this.running = running;
                }

                public boolean isPause() {
                    return pause;
                }
            }


            @Override
            public void mousePressed(MouseEvent e) {
                if (main.isSelectAndRotate()) {
                    System.out.println("Mouse Pressed");
                    System.out.println("Mouse starts dragging rotating");
                    mousePoint3D  = zRunner.getzBuffer().clickAt(e.getX(), e.getY());
                    representable = zRunner.getzBuffer().representableAt(e.getX(), e.getY());
                    arcBall = new ArcBall2(getzRunner().getzBuffer().camera(), mousePoint3D, 10.0, getzRunner().getzBuffer());
                    arcBall.init(representable);
                    if (threadDrawing == null) {
                        threadDrawing = new ThreadDrawing();
                        threadDrawing.start();
                    }
                } else if (main.getUpdateView().getzRunner().isGraphicalEditing()) {
                    System.out.println("Mouse Pressed");
                    List<ModelBrowser.Cell> cellList;
                    cellList = new ModelBrowser(getzRunner().getzBuffer(), main.getDataModel().getScene(), Point3D.class).getObjects();
                    if (cellList != null)
                        cellList.forEach(cell -> {
                            Point point = getzRunner().getzBuffer().camera().coordonneesPoint2D((Point3D) cell.pRot
                            ,
                                    getzRunner().getzBuffer());
                            if (e != null && point != null &&
                                    e.getX() - 2 < point.getX() && e.getX() + 2 > point.getX()
                                    && e.getY() - 2 < point.getY() && e.getY() + 2 > point.getY()) {
                                mousePoint = point;
                                mousePoint3D= cell.pRot;
                                mousePoint3Dorig = (Point3D) cell.o;
                                if (threadDrawing == null) {
                                    threadDrawing = new ThreadDrawing();
                                    threadDrawing.start();
                                }
                                main.getGraphicalEdit2().add(cell.pRot);
                                main.getGraphicalEdit2().getCurrentSelection().add(cell.pRot);
                            }
                        });

                }
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                if (main.getUpdateView().getzRunner().isGraphicalEditing()) {
                    if (mousePoint3D != null) {
                        System.out.println("Mouse Released");
                        mousePoint3D.changeTo(getzRunner().getzBuffer().invert((int) e.getPoint().getX(), (int) e.getPoint().getY(), mousePoint3D, getzRunner().getzBuffer().camera()));
                        System.out.println(mousePoint3D);
                    }
                } else if(main.isSelectAndRotate()) {
                    if(arcBall.matrix()!=null) {
                        System.out.println("Mouse Released");
                        representable.getRotation().getElem().getRot().setElem(arcBall.matrix());
                        System.out.println("Matrix changed = " + representable.getRotation().getElem().getRot().getElem());
                        representable.getRotation().getElem().getCentreRot().setElem(mousePoint3D);
                        System.out.println("centreRot changed" + representable.getRotation().getElem().getCentreRot().getElem());
                        System.out.println("class re" + representable.getClass());
                    } else {
                        System.out.println("Matrix == null");
                    }
                }
                if (threadDrawing != null) {
                    threadDrawing.setRunning(false);
                    threadDrawing = null;
                }
                mousePoint3Dorig = null;
                mousePoint3D = null;
                mousePoint = null;
            }
        });
    }

    private void drawPoint(Point mousePoint) {
        for (int i = -2; i <= 2; i++)
            for (int j = -2; j <= 2; j++)
                ((BufferedImage) main.getUpdateView().getzRunner().getLastImage())
                        .setRGB((int) mousePoint.getX() + i, (int) mousePoint.getY() + j, Color.RED.getRGB());

    }


    public void setFF(Main ff) {
        this.main = ff;
        this.getzRunner().setMain(ff);
    }

    private FunctionView view;

    public FunctionView getView() {
        return view;
    }

    public void setView(FunctionView view) {
        FunctionView old = this.view;
        this.view = view;

        firePropertyChange("view", old, view);
    }

    public void afterSet() {

    }

    private ZRunnerMain zRunner;

    public ZRunnerMain getzRunner() {
        return zRunner;
    }

    public void setzRunner(ZRunnerMain zRunner) {
        ZRunnerMain old = this.zRunner;
        this.zRunner = zRunner;
        getView().addPropertyChangeListener(getzRunner());
        addPropertyChangeListener(getzRunner());
        firePropertyChange("zRunner", old, zRunner);
    }

    @Override
    public void initValues(Representable representable) {
        this.currentRepresentable = representable;
    }


}
