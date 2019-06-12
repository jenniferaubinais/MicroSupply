import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;


//*********************************************************************************
//*********************************************************************************
public class MyDLL {

	final static String Version = "5.0.0";
	final static boolean initVisible = false;
	
	final static int allWidth = 1200;
	final static int leftWidth = 90;
	final static int middleWidth = 1000;
	final static int rigthWidth = 90;
	
	final static int allHeight = 660;
	final static int topHeight = 40;
	final static int middleHeight = 404;
	final static int bottomDrawHeight = 50;
	final static int bottomObjetHeight = 118;
	
	final static int nbValues = 1000;
	final static int valCalDiff = 400;
	
	protected static Shell shellMS = null;
	protected static String stReturn = "ERROR";
	protected static int[] values = new int[nbValues];
	protected static int compteur = 0;
	protected static int iWhite = 50;
	protected static int iScale = 1;
	protected static String stError = ""; 
	protected static boolean FlagReturn = false;
	protected static String stKey = ""; 
	protected static boolean FlagTouch = false;
	protected static boolean FlagScale = false;
	protected static boolean FlagClean = false;
	protected static boolean FlagScreen = false;
	protected static boolean FlagAutoMaxMin = false;
	protected static boolean FlagAmpereMaxMin = false;
	protected static boolean FlagClose = false;
	protected static int valScaleInside = 0;
	protected static int valScaleMove = 0;
	protected static int valMax = 0;  
	protected static int valMin = 1000000;   
	
	protected static Combo cbListCOM = null;
	protected static Button btnStart  = null;
	protected static Button btnStop = null; 
	protected static Button btn1S = null; 
	protected static Button btn2S = null; 
	protected static Button btn4S = null; 
	protected static Button btnSave = null; 
	protected static Button btnRefresh = null; 
	protected static Button btnExit = null; 
	protected static Button btnHelp = null; 
	protected static Scale ScaleInside = null;
	protected static Scale ScaleMove = null;
	protected static Text textMAX = null;
	protected static Text textMIN = null;
	protected static Label lblCOM = null;
	protected static Label lblCopyright = null;
	protected static Label lblMAX = null;
	protected static Label lblMIN = null;
	protected static Label lblZero = null;
	protected static Label lblVert1 = null;
	protected static Label lblVert2 = null;
	protected static Label lblVert3 = null;
	protected static Label lblVert4 = null;
	protected static Label lblHorZero = null;
	protected static Label lblHor1 = null;
	protected static Label lblHor2 = null;
	protected static Label lblHor3 = null;
	protected static Label lblHor4 = null;
	protected static Label lblScale = null;

	protected static Color colorGray = null;
	protected static Color colorTransparent = null;
	
	//private Oscilloscope oscilloscope;

	//*********************************************************************************
	// MAIN
	//*********************************************************************************
	public static void main(String[] args) {
		initScreenJA();
	   	// LOOP
		while (!testScreenReady()) {}
		addCOMPort("");
		setVisible("START",true);
		setVisible("MAX",true);
		setVisible("MIN",true);
		setValueX("0.25", "0.5", "0.75", "1s");
        setValueY("1", "2", "3", "4", "µA");
        setAutoMaxMin(true,true);
        addCOMPort("COM1,");
        setVisible("1S",true);
        setVisible("2S",true);
        setEnable("2S",false);
        setVisible("4S",true);
        setEnable("4S",true);
		int decal = 0;
		int Multi = 1;
		writeMessage("ERROR", "Error", "Error COM Port !");
	   	while( true)
	   	{
	   		int calcul = (int) (Math.sin((float)(compteur+(decal*50))/100) * 100);
	   		addData((calcul + 100)*Multi);
	   		if (compteur == 0)
	   		{
	   			compteur = 0;
	   			decal++;
	   			if (decal < 60)
	   			{
	   				if ((decal%10) == 0) Multi = Multi * 10;
	   			}
	   			else
	   			{
	   				if ((decal%10) == 0) Multi = Multi / 10;
	   				if (decal > 600) decal = 0;
	   			}
	   		}
	   		try
	   		{
	   			Thread.sleep(1);
	   		}
	   		catch(Exception ex)
	   		{}
	   	}
	}
	//*********************************************************************************
	//  Init Screen
	//*********************************************************************************
	public static boolean initScreenJA() 
	{
		Runnable myRunnable = new Runnable(){
		    public void run(){
		    	try {
		    		MyDLL.open();
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
		return true;
	}
	//*********************************************************************************
	//  Open the window
	//*********************************************************************************
	private static void open() {
		Display display = new Display ();
	    Shell shell = new Shell (display, SWT.CLOSE);
	    shell.setText("MicroSupply " + Version);
	    shell.setSize(allWidth, allHeight);
	    
	    RowLayout layout = new RowLayout(SWT.HORIZONTAL);
	    layout.spacing = 0;
	    layout.marginTop = 0;
	    layout.marginRight = 0;
	    layout.marginLeft = 0;
	    layout.marginBottom = 0;
	    shell.setLayout(layout);

	    colorTransparent = display.getSystemColor(SWT.COLOR_TRANSPARENT);
	    colorGray = display.getSystemColor(SWT.COLOR_GRAY);
	    
	    // Top   
		Composite cC0H0 = new Composite(shell, SWT.NONE);
		cC0H0.setLayoutData(new RowData(allWidth, topHeight));
		//cC0H0.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		GraphPlusNebulaTop(cC0H0, leftWidth, topHeight);
		// Middle
		Composite cC0H1 = new Composite(shell, SWT.NONE);
		cC0H1.setLayoutData(new RowData(leftWidth, middleHeight));
		//cC0H1.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		GraphPlusNebulaLeft(cC0H1, leftWidth, 2, middleHeight);
	    //
		Composite cC1H1 = new Composite(shell, SWT.NONE);
	    //cC1H1.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		cC1H1.setLayoutData(new RowData(middleWidth, middleHeight));
	    cC1H1.setLayout(new RowLayout());
	    cC1H1.addPaintListener(new PaintListener(){ 
	           public void paintControl(PaintEvent e){ 
	        	e.gc.setLineWidth(4);
	    		e.gc.drawLine(2, 0, 2, middleHeight);
	           }
	    });
		// NEBULA Oscilloscope
	    MyDLL osci = new MyDLL();
	    osci.createControl(cC1H1);
	    //
	    Composite cC2H1 = new Composite(shell, SWT.NONE);
	    cC2H1.setLayoutData(new RowData(rigthWidth, middleHeight));
		//cC2H1.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
	    cC2H1.addPaintListener(new PaintListener(){ 
	           public void paintControl(PaintEvent e){ 
	        	e.gc.setLineWidth(4);
	    		e.gc.drawLine(20, middleHeight, 10, middleHeight - 10);
	           }
	    });
	    
	    // Bottom draw
		Composite cC0H2 = new Composite(shell, SWT.NONE);
		cC0H2.setLayoutData(new RowData(allWidth, bottomDrawHeight));
		//cC0H2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));  
	    GraphPlusNebulaBottom(cC0H2, leftWidth, 2, middleWidth);
		// Bottom Keys
		Composite cC0H3 = new Composite(shell, SWT.NONE);
		cC0H3.setLayoutData(new RowData(allWidth, bottomObjetHeight));
		//cC0H3.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
	    CreateBottom(cC0H3);  
	    
	    shell.addListener(SWT.Close, new Listener() {
	        public void handleEvent(Event event) {
	        	FlagClose = true; 	
	        }
	      });
	    
	    shellMS = shell;
	    shell.open();
	    FlagScreen = true;
	    while (!shell.isDisposed ()) {
	        if (!display.readAndDispatch ()) display.sleep ();
	    }
	    display.dispose (); 
	}
	//*********************************************************************************
	// Oscilloscope
	//*********************************************************************************
	private Control createControl(Composite parent) {
		OscilloscopeDispatcher dsp = new OscilloscopeDispatcher()
		{
			@Override
			public void hookBeforeDraw(Oscilloscope oscilloscope, int counter) {
			}
			@Override
			public void hookAfterDraw(Oscilloscope oscilloscope, int counter) {
				// System.out.println(System.nanoTime() - timer);
			}
			@Override
			public int getDelayLoop() {
				return 0;
			}
			@Override
			public boolean getFade() {
				return false;
			}
			@Override
			public int getTailSize() {
				return Oscilloscope.TAILSIZE_MAX;
			}
		};
		//
		final Oscilloscope scope = new Oscilloscope(1, dsp, parent, SWT.WRAP)
		{	
			@Override
			protected void paintControl(PaintEvent e) {
				GC gcMem = e.gc;
				gcMem.setForeground(getForeground(0));
				gcMem.setAdvanced(true);
				gcMem.setLineWidth(getLineWidth(0));
				int ValMaxTempo = 0;  
				int ValMinTempo = 1000000;  
				valMax = 0;
				valMin = 1000000;
				for (int t = 0; t < 999; t++)
				{
					if (values[t] != -1)
					{
						if (!FlagClean)
						{
							if (iWhite != 0)
							{
								if ((t< compteur-1) || t > (compteur + iWhite))
								{
									if (iScale == 0)
									{
										if ((values[t]*10) > ValMaxTempo) ValMaxTempo = values[t]*10;
										if ((values[t]*10) < ValMinTempo) ValMinTempo = values[t]*10;
										gcMem.drawLine(t, valCalDiff - (values[t]*10), t+1, valCalDiff - (values[t+1]*10));
									}
									else
									{
										if ((values[t]/iScale) > ValMaxTempo) ValMaxTempo = values[t]/iScale;
										if ((values[t]/iScale) < ValMinTempo) ValMinTempo = values[t]/iScale;
										gcMem.drawLine(t, valCalDiff - (values[t]/iScale), t+1, valCalDiff - (values[t+1]/iScale));
									}
								}
							}
							else
							{
								if (iScale == 0)
								{
									if ((values[t]*10) > ValMaxTempo) ValMaxTempo = values[t]*10;
									if ((values[t]*10) < ValMinTempo) ValMinTempo = values[t]*10;
									gcMem.drawLine(t, valCalDiff - (values[t]*10), t+1, valCalDiff - (values[t+1]*10));
								}
								else
								{
									if ((values[t]/iScale) > ValMaxTempo) ValMaxTempo = values[t]/iScale;
									if ((values[t]/iScale) < ValMinTempo) ValMinTempo = values[t]/iScale;
									gcMem.drawLine(t, valCalDiff - (values[t]/iScale), t+1, valCalDiff - (values[t+1]/iScale));
								}								
							}
							// find Min and Max
							if (values[t] > valMax)
							{
								valMax = values[t];
							}
							if (values[t] < valMin)
							{
								valMin = values[t];
							}
						}
						else
						{
							gcMem.drawLine(t, values[t], t+1, values[t+1]);
							valMax = -1;
							valMin = -1;
						}
					}
				}
				//
				if ((valMax != -1) && (FlagAutoMaxMin))
				{
					if (FlagAmpereMaxMin)
					{
						if (valMax < 1000)
						{
							textMAX.setText(Integer.toString(valMax) + " µA");
						}
						else
						{
							float fMax = (float)valMax / 1000;
							if (valMax < 10000)
							{
								String stMax = String.format ("%.2f", fMax);
								textMAX.setText(stMax + " mA");
							}
							else
							{
								if (valMax <= 40000)
								{
									String stMax = String.format ("%.1f", fMax);
									textMAX.setText(stMax + " mA");
								}
							}
						}
						if (valMin < 1000)
						{
							textMIN.setText(Integer.toString(valMin) + " µA");
						}
						else
						{
							float fMin = (float)valMin / 1000;
							if (valMin < 10000)
							{
								String stMin = String.format ("%.2f", fMin);
								textMIN.setText(stMin + " mA");
							}
							else
							{
								if (valMax <= 40000)
								{
									String stMin = String.format ("%.1f", fMin);
									textMIN.setText(stMin + " mA");
								}
							}
						}
					}
					else
					{
						textMAX.setText(Integer.toString(valMax));
						textMIN.setText(Integer.toString(valMin));
					}
				}
				else
				{
					textMAX.setText("");
					textMIN.setText("");
				}
				//
				if (ValMaxTempo > 400)
				{
					if (iScale == 0)
					{
						iScale = 1;
					}
					else
					{
						iScale = iScale *10;
					}
					FlagScale = true; 
				}
				if ((ValMaxTempo < 40) && (ValMaxTempo >= 0))
					{
						if (iScale != 0)
						{
							if (iScale == 1)
							{
								iScale = 0;
							}
							else
							{
								iScale = iScale / 10;
							}
							FlagScale = true; 
						}
					}
					FlagClean = false;
				}		
			};	
		scope.setLayoutData(new RowData(middleWidth, middleHeight));
		scope.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x);
			}
		});
		scope.addStackListener(0, getStackAdapter());
		scope.setForeground(0,
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		scope.getDispatcher(0).dispatch();
		return scope;
	}
	//*********************************************************************************
	//  Read the status of the main screen
	//*********************************************************************************
	public static boolean testScreenReady()
	{
		int ComptWait = 0;
		while (!FlagScreen) {
	   		try
	   		{
	   			Thread.sleep(100);
	   		}
	   		catch(Exception ex)
	   		{}
	   		ComptWait += 1;
	   		if (ComptWait == 100) FlagScreen = true;
		}
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try
					{
						lblMIN.getEnabled();
					}
					catch (Exception Ex)
					{
						FlagReturn = false;
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
		}
		if (ComptWait == 100)
		{
			FlagScreen = true;
		}
		return FlagReturn;
	}
	//*********************************************************************************
	// Draw Bottom all Keys, Labels ...
	//*********************************************************************************
	private static void CreateBottom(Composite composite)
	{
		try
		{
			//
			lblCOM = new Label(composite, SWT.NONE);
			lblCOM.setAlignment(SWT.RIGHT);
			lblCOM.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			lblCOM.setVisible(initVisible);
			lblCOM.setBounds(60, 5, 99, 20);
			lblCOM.setText("Port COM :");
			//
			lblCopyright = new Label(composite, SWT.NONE);
			lblCopyright.setBounds(30, 90, 260, 15);
			lblCopyright.setText("(c) Elektor 170464 - (c) J.A. 2019 - Version " + Version);
			//
			cbListCOM = new Combo(composite, SWT.READ_ONLY);
			cbListCOM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					stKey = cbListCOM.getText() + ",";
					FlagTouch = true;
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					stKey = cbListCOM.getText() + ",";
					FlagTouch = true;
				}
			});
			cbListCOM.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			cbListCOM.setVisible(initVisible);
			cbListCOM.setEnabled(false);
			cbListCOM.setBounds(165, 5, 120, 25);
			//
			btnRefresh = new Button(composite, SWT.NONE);
			btnRefresh.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "REFRESH,";
				}
			});
			btnRefresh.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btnRefresh.setVisible(initVisible);
			btnRefresh.setTouchEnabled(true);
			btnRefresh.setBounds(165, 40, 99, 40);
			btnRefresh.setText("REFRESH");
			//
			btnStart = new Button(composite, SWT.NONE);
			btnStart.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "START,";
				}
			});
			btnStart.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btnStart.setVisible(initVisible);
			btnStart.setTouchEnabled(true);
			btnStart.setBounds(310, 5, 80, 40);
			btnStart.setText("START");
			//
			btnStop = new Button(composite, SWT.NONE);
			btnStop.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "STOP,";
				}
			});
			btnStop.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btnStop.setVisible(initVisible);
			btnStop.setTouchEnabled(true);
			btnStop.setBounds(310, 55, 80, 40);
			btnStop.setText("STOP");
			//
			lblMAX = new Label(composite, SWT.NONE);
			lblMAX.setAlignment(SWT.RIGHT);
			lblMAX.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			lblMAX.setVisible(initVisible);
			lblMAX.setBounds(395, 10, 102, 30);
			lblMAX.setText("Max value :");
			//
			textMAX = new Text(composite, SWT.BORDER);
			textMAX.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			textMAX.setVisible(initVisible);
			textMAX.setEditable(false);
			textMAX.setBounds(505, 10, 80, 30);
			//
			lblMIN = new Label(composite, SWT.NONE);
			lblMIN.setText("Min value :");
			lblMIN.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			lblMIN.setVisible(initVisible);
			lblMIN.setAlignment(SWT.RIGHT);
			lblMIN.setBounds(395, 50, 102, 30);
			//
			textMIN = new Text(composite, SWT.BORDER);
			textMIN.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			textMIN.setVisible(initVisible);
			textMIN.setEditable(false);
			textMIN.setBounds(505, 50, 80, 30);
			//
			btn1S = new Button(composite, SWT.NONE);
			btn1S.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "1S,";
				}
			});
			btn1S.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btn1S.setVisible(initVisible);
			btn1S.setTouchEnabled(true);
			btn1S.setBounds(615, 5, 90, 30);
			btn1S.setText("1 second");
			//
			btn2S = new Button(composite, SWT.NONE);
			btn2S.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "2S,";
				}
			});
			btn2S.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btn2S.setVisible(initVisible);
			btn2S.setTouchEnabled(true);
			btn2S.setBounds(710, 5, 90, 30);
			btn2S.setText("2 seconds");
			//
			btn4S = new Button(composite, SWT.NONE);
			btn4S.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "4S,";
				}
			});
			btn4S.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btn4S.setVisible(initVisible);
			btn4S.setTouchEnabled(true);
			btn4S.setBounds(805, 5, 90, 30);
			btn4S.setText("4 seconds");
			//
			ScaleInside = new Scale(composite, SWT.NONE);
			ScaleInside.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(SelectionEvent e) {
		    		valScaleInside = ScaleInside.getSelection();
					FlagTouch = true;
					stKey = "INSIDE,";
		    	}
		    });
			ScaleInside.setMaximum(12);
			ScaleInside.setVisible(initVisible);
			ScaleInside.setBounds(615, 40, 285, 30);
			//
			ScaleMove = new Scale(composite, SWT.NONE);
			ScaleMove.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(SelectionEvent e) {
		    		valScaleMove = ScaleMove.getSelection();
					FlagTouch = true;
					stKey = "MOVE,";
		    	}
		    });
			ScaleMove.setMaximum(3);
			ScaleMove.setVisible(initVisible);
			ScaleMove.setBounds(615, 70, 90, 30);
			//
			btnSave = new Button(composite, SWT.NONE);
			btnSave.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "SAVE,";
				}
			});
			btnSave.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btnSave.setVisible(initVisible);
			btnSave.setTouchEnabled(true);
			btnSave.setBounds(930, 5, 80, 40);
			btnSave.setText("SAVE");
			//
			btnExit = new Button(composite, SWT.NONE);
			btnExit.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "EXIT,";
				}
			});
			btnExit.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btnExit.setVisible(initVisible);
			btnExit.setTouchEnabled(true);
			btnExit.setBounds(1030, 5, 80, 40);
			btnExit.setText("EXIT");
			//
			btnHelp = new Button(composite, SWT.NONE);
			btnHelp.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FlagTouch = true;
					stKey = "HELP,";
				}
			});
			btnHelp.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			btnHelp.setVisible(initVisible);
			btnHelp.setTouchEnabled(true);
			btnHelp.setBounds(1030, 55, 80, 40);
			btnHelp.setText("HELP");
		}
		catch(Exception ex)
		{}
	}
	//*********************************************************************************
	// Create line on bottom
	//*********************************************************************************
	private static void GraphPlusNebulaBottom(Composite compositeGPN, int GraphX, int GraphY,  int GraphWidth)
	{		
	    compositeGPN.addPaintListener(new PaintListener(){ 
	           public void paintControl(PaintEvent e){ 
	        	e.gc.setLineWidth(4);
	    		e.gc.drawLine(GraphX - 15, GraphY, GraphX + GraphWidth + 20, GraphY);
	    		e.gc.drawLine(GraphX + GraphWidth + 20, GraphY, GraphX + GraphWidth + 10, GraphY + 10);
	    		e.gc.drawLine(GraphX + GraphWidth + 20, GraphY, GraphX + GraphWidth + 10, GraphY - 10);
	    		e.gc.drawLine(GraphX, GraphY, GraphX, GraphY + 18);
	    		e.gc.drawLine(GraphX + (GraphWidth/4), GraphY, GraphX + (GraphWidth/4), GraphY + 18);
	    		e.gc.drawLine(GraphX + (GraphWidth/2), GraphY, GraphX + (GraphWidth/2), GraphY + 18);
	    		e.gc.drawLine(GraphX + ((3*GraphWidth)/4), GraphY, GraphX + ((3*GraphWidth)/4), GraphY + 18);
	    		e.gc.drawLine(GraphX + GraphWidth, GraphY, GraphX + GraphWidth, GraphY + 18);
	           } 
	       }); 
	    lblZero = new Label(compositeGPN, SWT.NONE);
	    lblZero.setText("0");
	    lblZero.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblZero.setAlignment(SWT.CENTER);
	    lblZero.setBounds(GraphX - 20, 22, 40, 20);
	    lblVert1 = new Label(compositeGPN, SWT.NONE);
	    lblVert1.setText("");
	    lblVert1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblVert1.setAlignment(SWT.CENTER);
	    lblVert1.setBounds(GraphX + (GraphWidth/4) - 15, 22, 30, 20);
	    lblVert2 = new Label(compositeGPN, SWT.NONE);
	    lblVert2.setText("");
	    lblVert2.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblVert2.setAlignment(SWT.CENTER);
	    lblVert2.setBounds(GraphX + (GraphWidth/2) - 15, 22, 30, 20);
	    lblVert3 = new Label(compositeGPN, SWT.NONE);
	    lblVert3.setText("");
	    lblVert3.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblVert3.setAlignment(SWT.CENTER);
	    lblVert3.setBounds(GraphX + ((3*GraphWidth)/4) - 15, 22, 30, 20);
	    lblVert4 = new Label(compositeGPN, SWT.NONE);
	    lblVert4.setText("");
	    lblVert4.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblVert4.setAlignment(SWT.CENTER);
	    lblVert4.setBounds(GraphX + GraphWidth - 20, 22, 40, 20);
	}
	//*********************************************************************************
	// Create line on left
	//*********************************************************************************
	private static void GraphPlusNebulaLeft(Composite compositeGPN, int GraphX, int GraphY, int GraphHeight)
	{
	    compositeGPN.addPaintListener(new PaintListener(){ 
	           public void paintControl(PaintEvent e){ 
	        	e.gc.setLineWidth(4);
	        	e.gc.drawLine(GraphX -1, 0, GraphX -1, GraphHeight);
	    		e.gc.drawLine(GraphX, GraphY, GraphX - 15, GraphY);
	    		e.gc.drawLine(GraphX, GraphY + (GraphHeight/4), GraphX - 15, GraphY + (GraphHeight/4));
	    		e.gc.drawLine(GraphX, GraphY + (GraphHeight/2), GraphX - 15, GraphY + (GraphHeight/2));
	    		e.gc.drawLine(GraphX, GraphY + ((3*GraphHeight)/4), GraphX - 15, GraphY + ((3*GraphHeight)/4));
	           } 
	       }); 
	    lblScale = new Label(compositeGPN, SWT.NONE);
	    lblScale.setText("");
	    lblScale.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblScale.setAlignment(SWT.RIGHT);
	    lblScale.setBounds(GraphX - 60,  GraphY, 40, 20);
	    lblHor1 = new Label(compositeGPN, SWT.NONE);
	    lblHor1.setText("");
	    lblHor1.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblHor1.setAlignment(SWT.RIGHT);
	    lblHor1.setBounds(GraphX - 60,  GraphY + ((3*GraphHeight)/4) - 10, 40, 20);
	    lblHor2 = new Label(compositeGPN, SWT.NONE);
	    lblHor2.setText("");
	    lblHor2.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblHor2.setAlignment(SWT.RIGHT);
	    lblHor2.setBounds(GraphX - 60,  GraphY + (GraphHeight/2) - 10, 40, 20);
	    lblHor3 = new Label(compositeGPN, SWT.NONE);
	    lblHor3.setText("");
	    lblHor3.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblHor3.setAlignment(SWT.RIGHT);
	    lblHor3.setBounds(GraphX - 60,  GraphY + (GraphHeight/4) - 10, 40, 20);
	    lblHorZero = new Label(compositeGPN, SWT.NONE);
	    lblHorZero.setText("0");
	    lblHorZero.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblHorZero.setAlignment(SWT.RIGHT);
	    lblHorZero.setBounds(GraphX - 40,  GraphY + GraphHeight - 17, 20, 20);
	}
	//*********************************************************************************
	// Graph around Nebula
	//*********************************************************************************
	private static void GraphPlusNebulaTop(Composite compositeGPN, int GraphWidth, int GraphY)
	{
		compositeGPN.addPaintListener(new PaintListener(){ 
	           public void paintControl(PaintEvent e){ 
	        	e.gc.setLineWidth(4);
	        	e.gc.drawLine(GraphWidth, GraphY - 20, GraphWidth, GraphY);
	        	e.gc.drawLine(GraphWidth, GraphY - 20, GraphWidth + 9, GraphY - 10);
	        	e.gc.drawLine(GraphWidth, GraphY - 20, GraphWidth - 9, GraphY - 10);
	           }
		});
	    lblHor4 = new Label(compositeGPN, SWT.NONE);
	    lblHor4.setText("");
	    lblHor4.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
	    lblHor4.setAlignment(SWT.RIGHT);
	    lblHor4.setBounds(GraphWidth - 60, GraphY - 20, 40, 20);
	}
	//*********************************************************************************
	// Set position
	//*********************************************************************************
	public static void setPosition(String stTouch, int oX, int oY, boolean oVisible) 
	{	
		if (stTouch == "COM") 
		{
			lblCOM.setVisible(oVisible);
			lblCOM.setBounds(oX, oY, 99, 20);
			cbListCOM.setVisible(oVisible);
			cbListCOM.setBounds(oX, oY, 120, 25);
		}
		if (stTouch == "START") 
		{
			btnStart.setVisible(oVisible);
			btnStart.setBounds(oX, oY, 80, 40);
		}
		if (stTouch == "STOP") 
		{
		}
		if (stTouch == "SAVE")
		{
		}
		if (stTouch == "1S1") 
		{
		}
		if (stTouch == "1S2") 
		{
		}
		if (stTouch == "1S3") 
		{
		}
		if (stTouch == "1S4") 
		{
		}
		if (stTouch == "2S1") 
		{
		}
		if (stTouch == "2S2") 
		{
		}
		if (stTouch == "4S") 
		{
		}
		if (stTouch == "REFRESH")
		{
		}
		if (stTouch == "EXIT") 
		{
		}
		if (stTouch == "MAX") 
		{
			//lblMAX
			//textMAX
		}
		if (stTouch == "MAX") 
		{
			//lblMIN
			//textMIN
		}
		if (stTouch == "COPYRIGHT") 
		{
//			lblCopyright
//			if (oX > (iLimitCopyrightX-100))
//			{
//				iCopyrightX = iLimitCopyrightX-120;
//			}
//			else
//			{
//				iCopyrightX = oX;
//			}
//			if (oY > (iLimitCopyrightY-40))
//			{
//				iCopyrightY = iLimitCopyrightY-90;
//			}
//			else
//			{
//				iCopyrightY = oY;
//			}
		} 
	}
	//*********************************************************************************
	//  Add les port COM
	//*********************************************************************************
	public static boolean addCOMPort(String listPort)
	{
		String[] parts = listPort.split(",");
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try
					{
						cbListCOM.setVisible(true);
						if (parts.length != 0)
						{
							if (parts.length == 1)
							{
								if (parts[0] == "")
								{
									cbListCOM.removeAll();
									cbListCOM.setEnabled(false);
									btnStart.setVisible(false);
								}
								else
								{
									cbListCOM.add(parts[0]);
									cbListCOM.select(0);
									cbListCOM.setEnabled(false);
									btnStart.setVisible(true);
								}
							}
							else
							{
								for(String part : parts)
								{
									cbListCOM.add(part);
								}
								cbListCOM.setEnabled(true);
								btnStart.setVisible(false);
							}
						}
					}
					catch (Exception Ex)
					{
						FlagReturn = false;
						stError = stError + "ERROR addCOM 1 : " + Ex + ",";
					}
				}
			});
		}
		catch (Exception Ex)
		{
			stError = stError + "ERROR addCOM 2 : " + Ex + ",";
			FlagReturn = false;
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static boolean addData(int data)
	{
		values[compteur] =  data;
		compteur++;
		if (compteur > 999)
		{
			compteur = 0;
		}
		return FlagTouch;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static void setToZero()
	{
		compteur = 0;
	}	
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static void setAutoMaxMin(boolean FlagMaxMin, boolean FlagAmpere)
	{
		if (FlagMaxMin)
		{
			FlagAutoMaxMin = true; 
			FlagAmpereMaxMin = FlagAmpere;
			setVisible("MAX", true);
			setVisible("MIN", true);			
		}
		else
		{
			FlagAutoMaxMin = false; 
			FlagAmpereMaxMin = FlagAmpere;
			setVisible("MAX", false);
			setVisible("MIN", false);
		}
	}	
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static void cleanScreen()
	{
		for (int x = 0; x < nbValues; x++)
		{
			values[x] =  -1;
		}	
	    compteur = 0;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static boolean setMax(String ValueMax)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try
					{
						textMAX.setText(ValueMax);
					}
					catch (Exception Ex)
					{
						FlagReturn = true;
						stError = stError + "ERROR SetMAX 1 : " + Ex + ",";
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = true;
			stError = stError + "ERROR SetMAX 2 : " + Ex + ",";
			return false;
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static boolean setMin(String ValueMin)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try
					{
						textMIN.setText(ValueMin);
					}
					catch (Exception Ex)
					{
						FlagReturn = true;
						stError = stError + "ERROR SetMIN 1 : " + Ex + ",";
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = true;
			stError = stError + "ERROR SetMIN 2 : " + Ex + ",";
			return false;
		}
		return FlagReturn;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static int getMax()
	{
			return valMax;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static int getMin()
	{
			return valMin;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static int getScaleMove()
	{
		return valScaleMove;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static int getScaleInside()
	{
		return valScaleInside;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static boolean getStatusScale()
	{
		return FlagScale;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static boolean getStatusClose()
	{
		return FlagClose;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static int getScale()
	{
		FlagScale = false;
		return iScale;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static boolean setZero(String Val)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try
					{
						lblZero.setText(Val);
					}
					catch (Exception Ex)
					{
						FlagReturn = false;
						stError = stError + "Set Zero 1 : " + Ex + ",";
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
			stError = stError + "Set Zero 2 : " + Ex + ",";
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static boolean setValueX(String Val1, String Val2, String Val3, String Val4)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try
					{
						lblVert1.setText(Val1);
						lblVert2.setText(Val2);
						lblVert3.setText(Val3);
						lblVert4.setText(Val4);
					}
					catch (Exception Ex)
					{
						FlagReturn = false;
						stError = stError + "Set Value X 1 : " + Ex + ",";
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
			stError = stError + "Set Value X 2 : " + Ex + ",";
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  
	//*********************************************************************************
	public static boolean setValueY(String Val1, String Val2, String Val3, String Val4, String ValScale)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try
					{
						lblHor1.setText(Val1);
						lblHor2.setText(Val2);
						lblHor3.setText(Val3);
						lblHor4.setText(Val4);
						lblScale.setText(ValScale);
					}
					catch (Exception Ex)
					{
						FlagReturn = false;
						stError = stError + "Set Value Y 1 : " + Ex + ",";
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
			stError = stError + "Set Value Y 2 : " + Ex + ",";

		}
		return FlagReturn;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static boolean getStatusKey()
	{
		return FlagTouch;
	}
	//*********************************************************************************
	//  Get the keys pressed
	//*********************************************************************************
	public static String getKeyValue()
	{
		FlagTouch = false;
		String stTempo = stKey;
		stKey = "";
		return stTempo;
	}
	//*********************************************************************************
	//  Get Error
	//*********************************************************************************	
	public static String getErrorValue()
	{
		String stTempo = stError;
		stError = "";
		return stTempo;
	}
	//*********************************************************************************
	// 
	//*********************************************************************************
	public static boolean setWhite(int valWhite)
	{
		if (valWhite < 200)
		{
			iWhite = valWhite;
			return true;
		}
		return false;
	}
	//*********************************************************************************
	//  Get the version of the DLL
	//*********************************************************************************
	public static String getVersion() 
	{	
		return Version;
	}
	//*********************************************************************************
	//  Set Visible
	//*********************************************************************************
	public static boolean setVisible(String stTouch, boolean FlagVisible)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (stTouch == "COM") 
					{
						lblCOM.setVisible(FlagVisible);
						cbListCOM.setVisible(FlagVisible);
					}
					if (stTouch == "STOP") btnStop.setVisible(FlagVisible);
					if (stTouch == "START") btnStart.setVisible(FlagVisible);
					if (stTouch == "SAVE") btnSave.setVisible(FlagVisible);
					if (stTouch == "1S") 
					{
						btn1S.setVisible(FlagVisible);
					}
					if (stTouch == "MOVE") 
					{
						ScaleMove.setVisible(FlagVisible);
					}
					if (stTouch == "2S")			
					{
						btn2S.setVisible(FlagVisible);
					}
					if (stTouch == "INSIDE") 
					{
						ScaleInside.setVisible(FlagVisible);
					}
					if (stTouch == "4S")
					{
						btn4S.setVisible(FlagVisible);
					}
					if (stTouch == "REFRESH") btnRefresh.setVisible(FlagVisible);
					if (stTouch == "EXIT") btnExit.setVisible(FlagVisible);
					if (stTouch == "HELP") btnHelp.setVisible(FlagVisible);
					if (stTouch == "MAX") 
					{
						lblMAX.setVisible(FlagVisible);
						textMAX.setVisible(FlagVisible);
					}
					if (stTouch == "MIN") 
					{
						lblMIN.setVisible(FlagVisible);
						textMIN.setVisible(FlagVisible);
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
			stError = stError + "ERROR Set Visible : " + Ex + ",";
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  Set Enable
	//*********************************************************************************
	public static boolean setEnable(String stTouch, boolean FlagEnable)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (stTouch == "COM") 
					{
						cbListCOM.setEnabled(FlagEnable);
					}
					if (stTouch == "1S") 
					{
						if (FlagEnable)
						{
							btn1S.setEnabled(true);
							btn1S.setBackground(colorTransparent);
						}
						else
						{
							btn1S.setEnabled(false);
							btn1S.setBackground(colorGray);
						}
					}
					if (stTouch == "2S")			
					{
						if (FlagEnable)
						{
							btn2S.setEnabled(true);
							btn2S.setBackground(colorTransparent);
						}
						else
						{
							btn2S.setEnabled(false);
							btn2S.setBackground(colorGray);
						}
					}
					if (stTouch == "4S")
					{
						if (FlagEnable)
						{
							btn4S.setEnabled(true);
							btn4S.setBackground(colorTransparent);
						}
						else
						{
							btn4S.setEnabled(false);
							btn4S.setBackground(colorGray);
						}
					}
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
			stError = stError + "ERROR Set Visible : " + Ex + ",";
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  Set Scale Inside 
	//*********************************************************************************
	public static boolean setInside(int iVal)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					ScaleInside.setMaximum(iVal);
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
		}
		return FlagReturn;
	}
	//*********************************************************************************
	//  Set Scale Move
	//*********************************************************************************
	public static boolean setMove(int iVal)
	{
		FlagReturn = true;
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					ScaleMove.setMaximum(iVal);
				}
			});
		}
		catch (Exception Ex)
		{
			FlagReturn = false;
		}
		return FlagReturn;
	}

	//*********************************************************************************
	//  Message with Yes or No
	//*********************************************************************************
	public static String writeMessageYesNo(String stText, String stMessage)
	{
		stReturn = "ERROR";
		try
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(shellMS, style);
					messageBox.setText(stText);
					messageBox.setMessage(stMessage);
					boolean bReturn = messageBox.open() == SWT.YES;    
					if (bReturn)
					{
						stReturn = "YES";
					}
					else
					{
						stReturn = "NO";
					}
				}
			});
		}
		catch (Exception Ex)
		{
		}
		return stReturn;
	}
	//*********************************************************************************
	//  Message ERROR, WARNING
	//*********************************************************************************
	public static void writeMessage(String stIcon, String stText, String stMessage)
	{
		switch(stIcon)
		{
		case "ERROR":
			try
			{
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						int style = SWT.APPLICATION_MODAL | SWT.ICON_ERROR | SWT.OK;
						MessageBox messageBox = new MessageBox(shellMS, style);
						messageBox.setText(stText);
						messageBox.setMessage(stMessage);
						messageBox.open();    
					}
				});
			}
			catch (Exception Ex) {}
			break;
		case "WARNING":
			try
			{
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						int style = SWT.APPLICATION_MODAL | SWT.ICON_WARNING | SWT.OK;
						MessageBox messageBox = new MessageBox(shellMS, style);
						messageBox.setText(stText);
						messageBox.setMessage(stMessage);
						messageBox.open();    
					}
				});
			}
			catch (Exception Ex) {}
			break;
		}
	}

	//*********************************************************************************
	// 
	//*********************************************************************************
	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			boolean init = false;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {
				if (!init) {
					init = true;
				}			
			}
		};
	}
}

