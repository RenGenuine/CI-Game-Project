package Game;

	import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

	class IceyGame
	{
		private class IceyPanel extends JPanel implements Runnable
		{
			final private int DOWNTILE = 7;
			final private int EXIT = 3;
			final private int ICE = 0;
			final private int KEYPRESSED = 1;
			final private int KEYRELEASED = 2;
			final private int KEYTYPED = 0;
			final private int LEFTTILE = 9;
			private int[][] map;
			final private int RIGHTTILE = 8;
			final private int SPEED = 16;
			final private int SPIKES = 5;
			final private int START = 2;
			private Thread thread;
			final private int TILE = 4;
			final private int UPTILE = 6;
			final private int WALL = 1;
			
			public IceyPanel()
			{
				try
				{
					C = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\SpriteFront.jpeg"));
					Ice = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\ICE.jpeg"));
					Sand = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\SAND.jpeg"));
					ArrowUp = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\ARROWUP.jpeg"));
					ArrowDown = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\ARROWDOWN.jpeg"));
					ArrowLeft = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\ARROWLEFT.jpeg"));
					ArrowRight = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\ARROWRIGHT.jpeg"));
					SpikeTile = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\SpikeTile.jpeg"));
					FinishTile = ImageIO.read(new File("C:\\Users\\Tyren\\Desktop\\Workspace\\Bast\\src\\Res\\PORTAL.jpeg"));
				}
				catch(IOException ex){
					
				}
				addKeyListener(new KeyAdapter()
					{
						@Override
						public void keyPressed(KeyEvent e)
						{
							keyEvent(KEYPRESSED, e);
						}
						
						@Override
						public void keyReleased(KeyEvent e)
						{
							keyEvent(KEYRELEASED, e);
						}
						
						@Override
						public void keyTyped(KeyEvent e)
						{
							keyEvent(KEYTYPED, e);
						}
					});
				setFocusable(true);
				setBackground(new Color(63, 63, 63));
				setDoubleBuffered(true);
				setVisible(true);
			
				 try {                
				      Ice = ImageIO.read(new File("ICE.jpeg"));
				     } catch (IOException ex) {
				            // handle exception...
				       }
			}
			
			@Override
			public void addNotify()
			{
				super.addNotify();
				gameInit();
			}
			
			private void gameInit()
			{
				if(thread == null)
				{
					updateMap();
					thread = new Thread(this);
					thread.start();
				}
			}
			
			private int[][] generateMap(int minW, int maxW, int minH, int maxH, int seed)
			{
				Random r = new Random(seed);
				int w = r.nextInt(maxW - minW + 1) + minW + 2;
				int h = r.nextInt(maxH - minH + 1) + minH + 2;
				int MINMOVES = 3;
				int MAXMOVES = 8;
				int PATH = -1;
				int[][] map = new int[h][w];
				int x, y;
				
				for(x = 0; x < w; x++)
				{
					map[0][x] = WALL;
					map[h - 1][x] = WALL;
				}
				for(y = 0; y < h; y++)
				{
					map[y][0] = WALL;
					map[y][w - 1] = WALL;
				}
				
				map[y = r.nextInt(h - 2) + 1][x = r.nextInt(w - 2) + 1] = START;
				for(int move = r.nextInt(MAXMOVES - MINMOVES + 1); move < MAXMOVES; move++)
				{
					int[] dirs = new int[4];
					int size = 0;
					if(map[y - 1][x] == ICE) //up
						dirs[size++] = 0;
					if(map[y][x + 1] == ICE) //right
						dirs[size++] = 1;
					if(map[y + 1][x] == ICE) //down
						dirs[size++] = 2;
					if(map[y][x - 1] == ICE) //left
						dirs[size++] = 3;
					if(size == 0) //stuck
						break;
					int max = 1;
					int dist;
					switch(dirs[r.nextInt(size)])
					{
						case 0: //up
							if(r.nextInt(20) == 0)
								map[y][x] = UPTILE;
							while(map[y - max][x] == ICE || (map[y - max][x] == PATH && map[y - max - 1][x] == ICE))
								max++;
							dist = r.nextInt(--max) + 1;
							for(int i = 1; i < dist; i++)
								map[y - i][x] = PATH;
							if(map[y - dist][x] == PATH)
								dist++;
							if((dist == max && map[y - dist - 1][x] != WALL) || map[y - dist - 1][x] == PATH || r.nextBoolean())
								map[y - dist][x] = TILE;
							else
							{
								map[y - dist][x] = PATH;
								map[y - dist - 1][x] = WALL;
							}
							y = y - dist;
							break;
						case 1: //right
							if(r.nextInt(20) == 0)
								map[y][x] = RIGHTTILE;
							while(map[y][x + max] == ICE || (map[y][x + max] == PATH && map[y][x + max + 1] == ICE))
								max++;
							dist = r.nextInt(--max) + 1;
							for(int i = 1; i < dist; i++)
								map[y][x + i] = PATH;
							if(map[y][x + dist] == PATH)
								dist++;
							if((dist == max && map[y][x + dist + 1] != WALL) || map[y][x + dist + 1] == PATH || r.nextBoolean())
								map[y][x + dist] = TILE;
							else
							{
								map[y][x + dist] = PATH;
								map[y][x + dist + 1] = WALL;
							}
							x = x + dist;
							break;
						case 2: //down
							if(r.nextInt(20) == 0)
								map[y][x] = DOWNTILE;
							while(map[y + max][x] == ICE || (map[y + max][x] == PATH && map[y + max + 1][x] == ICE))
								max++;
							dist = r.nextInt(--max) + 1;
							for(int i = 1; i < dist; i++)
								map[y + i][x] = PATH;
							if(map[y + dist][x] == PATH)
								dist++;
							if((dist == max && map[y + dist + 1][x] != WALL) || map[y + dist + 1][x] == PATH || r.nextBoolean())
								map[y + dist][x] = TILE;
							else
							{
								map[y + dist][x] = PATH;
								map[y + dist + 1][x] = WALL;
							}
							y = y + dist;
							break;
						case 3: //left
							if(r.nextInt(20) == 0)
								map[y][x] = LEFTTILE;
							while(map[y][x - max] == ICE || (map[y][x - max] == PATH && map[y][x - max - 1] == ICE))
								max++;
							dist = r.nextInt(--max) + 1;
							for(int i = 1; i < dist; i++)
								map[y][x - i] = PATH;
							if(map[y][x - dist] == PATH)
								dist++;
							if((dist == max && map[y][x - dist - 1] != WALL) || map[y][x - dist - 1] == PATH || r.nextBoolean())
								map[y][x - dist] = TILE;
							else
							{
								map[y][x - dist] = PATH;
								map[y][x - dist - 1] = WALL;
							}
							x = x - dist;
							break;
					}
				}
				map[y][x] = EXIT;
				for(x = 0; x < w; x++)
					for(y = 0; y < h; y++)
						if(map[y][x] == ICE)
						{
							int random = r.nextInt(100);
							if(random < 70)
								map[y][x] = ICE;
							else
								if(random < 75)
									map[y][x] = SPIKES;
								else
									if(random < 90)
										map[y][x] = WALL;
									else
										map[y][x] = UPTILE + r.nextInt(4);
						}
						else
							if(map[y][x] == PATH)
								if(r.nextInt(10) < 9)
									map[y][x] = ICE;
								else
									map[y][x] = TILE;
				return map;
			}
			
			private void keyEvent(int type, KeyEvent event)
			{
				if(type == KEYPRESSED && moving == 0)
					switch(event.getKeyCode())
					{
						case KeyEvent.VK_W:
							move(1);
							break;
						case KeyEvent.VK_D:
							move(2);
							break;
						case KeyEvent.VK_S:
							move(3);
							break;
						case KeyEvent.VK_A:
							move(4);
							break;
						default:
							System.out.println(event.getKeyCode());
					}
				else
					if(type == KEYRELEASED)
						switch(event.getKeyCode())
						{
							case KeyEvent.VK_R:
								resetPlayer();
								break;
							case KeyEvent.VK_ENTER:
								updateMap();
								break;
						}
			}
			
			private void move(int m)
			{
				moving = m;
				int x = playerX / TILESIZE;
				int y = playerY / TILESIZE;
				switch(m)
				{
					case 1: //up
						if(map[y - 1][x] != WALL)
							playerY -= SPEED;
						else
							moving = 0;
						break;
					case 2: //right
						if(map[y][x + 1] != WALL)
							playerX += SPEED;
						else
							moving = 0;
						break;
					case 3: //down
						if(map[y + 1][x] != WALL)
							playerY += SPEED;
						else
							moving = 0;
						break;
					case 4: //left
						if(map[y][x - 1] != WALL)
							playerX -= SPEED;
						else
							moving = 0;
						break;
				}
			}
			
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				for(int y = 0; y < map.length; y++)
				{
					for(int x = 0; x < map[0].length; x++)
						PaintTile(x, y, g);
					/*if(playerY <= y * TILESIZE && playerY > (y - 1) * TILESIZE) */
						 
				}
				paintPlayer(g);
				
				paintChildren(g);
			}
			
			//public void actionPerformed(ActionEvent e)
			//{
			//	System.out.println(e.getActionCommand());
			//}
			
			private void paintPlayer(Graphics g)
			{
				int baseX = GAMEWIDTH / 2 - TILESIZE / 2;
				int baseY = (GAMEHEIGHT - GUIHEIGHT) / 2 - TILESIZE / 2;
				
				g.drawImage(C, baseX, baseY, null);
				
				//BufferedImage C = ImageIO.read(getClass().getResource(""));
				//ImageIcon image = new ImageIcon(C);
				
				
				/*g.setColor(new Color(255, 255, 0));
				g.fillRect(baseX + TILESIZE / 8, baseY + TILESIZE / 8, TILESIZE * 3 / 4, TILESIZE * 3 / 4);
				g.setColor(new Color(255, 127, 0));
				g.fillRect(baseX + TILESIZE / 4, baseY + TILESIZE / 4, TILESIZE / 2, TILESIZE / 2);
				g.setColor(new Color(255, 0, 0));
				g.fillRect(baseX + TILESIZE * 3 / 8, baseY + TILESIZE * 3 / 8, TILESIZE / 4, TILESIZE / 4);*/
			}
			
			private void PaintTile(int x, int y, Graphics g)
			{
				int baseX = x * TILESIZE - playerX - TILESIZE / 2 + GAMEWIDTH / 2;
				int baseY = y * TILESIZE - playerY - TILESIZE / 2 + (GAMEHEIGHT - GUIHEIGHT) / 2;
				switch(map[y][x])
				{
					case ICE:
						 g.drawImage(Ice, baseX, baseY, null);
						 break;
						/*g.setColor(new Color(63, 63, 255));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);
						break;*/
					case WALL:
						g.setColor(new Color(95, 95, 95));
						g.fillRect(baseX, baseY - TILESIZE / 4, TILESIZE, TILESIZE);
						g.setColor(new Color(127, 127, 127));
						g.fillRect(baseX, baseY + TILESIZE * 3 /4, TILESIZE, TILESIZE / 4);
						break;
					case START:
						g.setColor(new Color(255, 0, 127));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);
						break;
					case EXIT:
						g.drawImage(FinishTile, baseX, baseY, null);
						/*g.setColor(new Color(0, 191, 0));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);*/
						break;
					case TILE:
						 g.drawImage(Sand, baseX, baseY, null);
						/*g.setColor(new Color(255, 255, 127));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);*/
						break;
					case SPIKES:
						g.drawImage(SpikeTile, baseX, baseY, null);
						/*g.setColor(new Color(255, 0, 0));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);*/
						break;
					case UPTILE:
						g.drawImage(ArrowUp, baseX, baseY, null);
						/*g.setColor(new Color(255, 255, 255));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);
						g.setColor(new Color(0, 191, 0));
						g.fillRect(baseX + TILESIZE / 4, baseY, TILESIZE / 2, TILESIZE / 2);*/
						break;
					case RIGHTTILE:
						g.drawImage(ArrowRight, baseX, baseY, null);
						/*g.setColor(new Color(255, 255, 255));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);
						g.setColor(new Color(0, 191, 0));
						g.fillRect(baseX + TILESIZE / 2, baseY + TILESIZE / 4, TILESIZE / 2, TILESIZE / 2);*/
						break;
					case DOWNTILE:
						g.drawImage(ArrowDown, baseX, baseY, null);
						/*g.setColor(new Color(255, 255, 255));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);
						g.setColor(new Color(0, 191, 0));
						g.fillRect(baseX + TILESIZE / 4, baseY + TILESIZE / 2, TILESIZE / 2, TILESIZE / 2);*/
						break;
					case LEFTTILE:
						g.drawImage(ArrowLeft, baseX, baseY, null);
						/*g.setColor(new Color(255, 255, 255));
						g.fillRect(baseX, baseY, TILESIZE, TILESIZE);
						g.setColor(new Color(0, 191, 0));
						g.fillRect(baseX, baseY + TILESIZE / 4, TILESIZE / 2, TILESIZE / 2);*/
						break;
				}
			}
			
			private void resetPlayer()
			{
				moving = 0;
				for(int x = 0; x < map[0].length; x++)
					for(int y = 0; y < map.length; y++)
						if(map[y][x] == START)
						{
							playerX = x * TILESIZE;
							playerY = y * TILESIZE;
						}
			}
			
			@Override
			public void run()
			{
				long old = System.currentTimeMillis();
				while(true)
				{
					tick();
					repaint();
					long delay = 1000 / FPS - (System.currentTimeMillis() - old);
					if(delay < 2)
						delay = 2;
					try
					{
						thread.sleep(delay);
					}
					catch(InterruptedException e)
					{
						System.out.println(e + "\nInterrupted");
					}
					old = System.currentTimeMillis();
				}
			}
			
			public void tick()
			{
				boolean move = playerX % TILESIZE != 0 || playerY % TILESIZE != 0;
				int x = playerX / TILESIZE;
				int y = playerY / TILESIZE;
				if(moving != 0)
					switch(moving)
					{
						case 1: //up
							if(move)
								playerY -= SPEED;
							else
								switch(map[y][x])
								{
									case ICE:
										move(moving);
										break;
									case START:
										move(moving);
										break;
									case EXIT:
										moving = 0;
										updateMap();
										break;
									case TILE:
										moving = 0;
										break;
									case SPIKES:
										moving = 0;
										resetPlayer();
										break;
									case UPTILE:
										move(1);
										break;
									case RIGHTTILE:
										move(2);
										break;
									case DOWNTILE:
										move(3);
										break;
									case LEFTTILE:
										move(4);
										break;
								}
							break;
						case 2: //right
							if(move)
								playerX += SPEED;
							else
								switch(map[y][x])
								{
									case ICE:
										move(moving);
										break;
									case START:
										move(moving);
										break;
									case EXIT:
										moving = 0;
										updateMap();
										break;
									case TILE:
										moving = 0;
										break;
									case SPIKES:
										moving = 0;
										resetPlayer();
										break;
									case UPTILE:
										move(1);
										break;
									case RIGHTTILE:
										move(2);
										break;
									case DOWNTILE:
										move(3);
										break;
									case LEFTTILE:
										move(4);
										break;
								}
							break;
						case 3: //down
							if(move)
								playerY += SPEED;
							else
								switch(map[y][x])
								{
									case ICE:
										move(moving);
										break;
									case START:
										move(moving);
										break;
									case EXIT:
										moving = 0;
										updateMap();
										break;
									case TILE:
										moving = 0;
										break;
									case SPIKES:
										moving = 0;
										resetPlayer();
										break;
									case UPTILE:
										move(1);
										break;
									case RIGHTTILE:
										move(2);
										break;
									case DOWNTILE:
										move(3);
										break;
									case LEFTTILE:
										move(4);
										break;
								}
							break;
						case 4: //left
							if(move)
								playerX -= SPEED;
							else
								switch(map[y][x])
								{
									case ICE:
										move(moving);
										break;
									case START:
										move(moving);
										break;
									case EXIT:
										moving = 0;
										updateMap();
										break;
									case TILE:
										moving = 0;
										break;
									case SPIKES:
										moving = 0;
										resetPlayer();
										break;
									case UPTILE:
										move(1);
										break;
									case RIGHTTILE:
										move(2);
										break;
									case DOWNTILE:
										move(3);
										break;
									case LEFTTILE:
										move(4);
										break;
								}
							break;
					}
			}
			
			private void updateMap()
			{
				map = generateMap(8, 16, 8, 16, (int)(Math.random() * Integer.MAX_VALUE));
				resetPlayer();
			}
		}
		public static void main(String[] args)
		{
			new IceyGame();
		}
		final private int FPS = 30;
		private JFrame frame;
		final private int GAMEHEIGHT = 600;
		final private int GAMEWIDTH = 800;
		final private int GUIHEIGHT = 100;
		final private int GUIWIDTH = 800;
		private BufferedImage Ice,C, Sand, ArrowUp, ArrowDown, ArrowLeft, ArrowRight, SpikeTile, FinishTile;
		private int moving;
		private IceyPanel panel;
		
		
		private int playerX, playerY;
		
		final private int TILESIZE = 64;
		
		protected IceyGame()
		{
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle("IceyGame");
			frame.setContentPane(panel = new IceyPanel());
			frame.setVisible(true);
			frame.setSize(GAMEWIDTH + frame.getInsets().left - 1, GAMEHEIGHT + frame.getInsets().top - 1);
			frame.setLocationRelativeTo(null);
			frame.setResizable(false);
		}
	}
