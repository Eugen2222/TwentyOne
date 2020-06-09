
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

public class Client implements ActionListener {
	private class ReadWorker extends SwingWorker<Void,Void> {
		private Socket socket = null;
		private ObjectInputStream inputStream = null;
		private Client parent;
		
		public ReadWorker(Socket s, Client parent) {
			this.socket = s;
			this.parent = parent;
			try {
				inputStream = new ObjectInputStream(this.socket.getInputStream());
			}catch(IOException e) {
				e.printStackTrace();
			}
			//set invisible by default
			
		}
		public Void doInBackground() {
			System.out.println("Started read worker");
	
			try {
				Object data;
				while((data = inputStream.readObject())!=null) { 
					System.out.println("receive message");
								
					if(parent.joinGame) {
						if (data instanceof CardTable) {
							System.out.println("receive table");
							final CardTable t = (CardTable)data;
							SwingUtilities.invokeLater(new Runnable() {
							    @Override
							    public void run() {
							    	parent.view.paintCard(t.getTable());
							    	System.out.println("paint table");
							    }
							});
						}
						
						else if (data instanceof String[]) {
							final String[] message=(String[])data;
							System.out.println("receive message"+ message[0]);
							for(String s :message) {
								System.out.print(s );
							}
							System.out.println();
							if(message[0].equals("Stage")) {
						    	System.out.println("receive stage"+message[1]);
								if(message[1].equals("-1")) {
									SwingUtilities.invokeLater(new Runnable() {
									    @Override
									    public void run() {
									    	parent.view.clearBtn();
		
									    }
									});
									
								}else if(message[1].equals("0")) {
									SwingUtilities.invokeLater(new Runnable() {
									    @Override
									    public void run() {
									    	parent.view.clearBtn();
									    	parent.view.readyB.setVisible(true);
									    }
									});
									
								}else if(message[1].equals("1")) {
									SwingUtilities.invokeLater(new Runnable() {
									    @Override
									    public void run() {
									    	parent.view.clearBtn();
									    	parent.view.dealB.setEnabled(true);
									    	parent.view.standB.setEnabled(true);
									    }
									});
									
								}else if(message[1].equals("-2")) {
									SwingUtilities.invokeLater(new Runnable() {
									    @Override
									    public void run() {
									    	parent.view.clearBtn();
									    	parent.view.burstPList.get(playerId).setVisible(false);
	
									    	parent.view.kickPList.get(playerId).setVisible(true);
									    	parent.view.update();
									    }
									});
									
									
								}
							}else if(message[0].equals("MInfo")) {
		
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {			
								    	System.out.println("info:"+message[1]);
								    	parent.view.mainInfoL.setText(message[1]);
								    	parent.view.update();
								    }
								});
								
							}else if(message[0].equals("SInfo")) {
		
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {			
								    	System.out.println("SInfo:"+message[1]);
								    	parent.view.setSubInfo(message[1]);
								    	parent.view.update();
								    }
								});
								
							}
							
							else if(message[0].equals("Dealer")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {
								    	for(int i =1; i < message.length;i++) {
								    		parent.view.setTopLabel(i-1,message[i]);							    		
										}	
								    }
								});
							}
							else if(message[0].equals("PlayersName")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {
								    	
								    	for(int i =1; i < message.length;i++) {
											parent.view.setName(i-1, (message[i]));
										}				
								    }
								});
							}else if(message[0].equals("Stakes")) {
		
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {			
								    	System.out.println("Stakes:");
								    	for(int i =1; i < message.length;i++) {
											parent.view.setStakes(i-1, (message[i]));
										}		
								    	parent.view.update();
								    }
								});
								
							}
							else if(message[0].equals("ClearBoard")) {
						    	System.out.println("Clear");
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {
										parent.view.clearBoard();			
								    }
								});
							}else if(message[0].equals("Status")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {
								    	for(int i =1; i < message.length;i++) {
											parent.view.setStatus(i-1, (message[i]));
										}					
								    }
								});
							}else if(message[0].equals("Ready")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {				
								    	for(int i =1; i < message.length;i++) {
								    		boolean b = (message[i].equals("true"))? true: false;	
								    		System.out.println("ready transt:"+ b);
											parent.view.setReady(i-1, b);
										}												
								    }
								});
							}
							else if(message[0].equals("HandValue")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {					   
								    	for(int i =1; i < message.length;i++) {
											parent.view.setCardValueLabel(i-1, (message[i]));
										}												
								    }
								});
							}
							else if(message[0].equals("Burst")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {									    	
								    	for(int i =1; i < message.length;i++) {
								    		boolean b = (message[i].equals("true"))? true: false;						    		
											parent.view.setBurst(i-1, b);
										}												
								    }
								});
							}
							else if(message[0].equals("BlackJack")) {
								SwingUtilities.invokeLater(new Runnable() {
								    @Override
								    public void run() {					 
		
								    	for(int i =1; i < message.length;i++) {
								    		boolean b = (message[i].equals("true"))? true: false;			
					
											parent.view.setBlackJack(i-1, b);
										}												
								    }
								});
							}
						}
					}
					else if (data instanceof String[]) {
						final String[] message=(String[])data;
						System.out.println("parent.joinGame=false"+ message[0]);					
						System.out.println("receive message"+ message[0]);
						if(message[0].equals("PlayerID")) {
							SwingUtilities.invokeLater(new Runnable() {
							    @Override
							    public void run() {
							    	System.out.println("receive message PlayerID!!!"+ message[1]);
							    	parent.playerId=Integer.parseInt(message[1]);
							    	parent.activateView(parent.playerId);
							    	String [] s = {"Load",Integer.toString(playerId)};
									try {
										outputStream.writeObject(s);
										System.out.println("write object Load");
									}catch(IOException ex) {
										ex.printStackTrace();
									}
							    }
							});						
						}
					}
				}
			}catch(ClassNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
			}finally {
				return null;
			}
		
		}
	}
	
	private int playerId = -1;
	private Socket server = null;
	private ObjectOutputStream outputStream;
	private View view;
	private String name;
	private String IP;
	boolean connected = false;
	private boolean joinGame =false;
	public Client() {
			
		this.view = new View();	
		while(name==null) {
			name = this.view.askName();
		}

		while(IP==null) {
			IP = this.view.askIP();
		}
		while(connect()==false) {
			IP = this.view.reAskIP();
		}

		

			view.init();
			view.clearBtn();
			try {
				outputStream = new ObjectOutputStream(server.getOutputStream());
				outputStream.writeObject(nameData(name));
			}catch(IOException e) {
				e.printStackTrace();
			}
			ReadWorker rw = new ReadWorker(server,this);
			rw.execute();
			this.view.addWindowListener(new WindowAdapter(){
				public synchronized void windowClosing(WindowEvent e){
					try {	     
			        	   	System.out.println("quit");
			        	   	String [] s = {"Quit",Integer.toString(playerId)};	            	
			            	outputStream.writeObject(s);
			            	System.out.println(s[0]);
			            	System.out.println("send quit");
			            	rw.cancel(true);
			            	rw.inputStream.close();
			            	rw.socket.close();
			            	
			            	outputStream.close();
			            	server.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}finally {	    			

			    		}
			       }        
				});

	}
		
	public String[] nameData(String n) {
		String[] s =new String[2];
		s[0]="Name";
		s[1]=n;
		return s;
	}
	
	private boolean connect() {
		try {
			server = new Socket(IP,8765);
			System.out.println("Connected");
			return true;
		}catch(IOException e) {
			e.printStackTrace();
			System.out.println("F");
			return false;
		}
	}
	

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.view.readyB) {
			String [] s = {"Ready",Integer.toString(playerId)};
			try {
				view.readyB.setVisible(false);
				outputStream.writeObject(s);
				System.out.println("write object");
			}catch(IOException ex) {
				ex.printStackTrace();
			}
		}else if(e.getSource() == this.view.dealB) {
			String [] s = {"Deal",Integer.toString(playerId)};
			try {
				view.clearBtn();
				outputStream.writeObject(s);
				System.out.println("write object");
			}catch(IOException ex) {
				ex.printStackTrace();
			}
		}else if(e.getSource() == this.view.standB) {
			String [] s = {"Stand",Integer.toString(playerId)};
			try {
				view.clearBtn();
				outputStream.writeObject(s);
				System.out.println("write object");
			}catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	
		
	}
	public static void main(String[] args) {
		new Client();
	}
	
	public synchronized void activateView(int youID) {
		joinGame=true;
    	
    	view.displayBoard(youID);
    	view.dealB.addActionListener(this);
    	view.standB.addActionListener(this);	
    	view.readyB.addActionListener(this);
    	

    	view.setName(youID,name);		
    	view.update();

		
	}
}


	
