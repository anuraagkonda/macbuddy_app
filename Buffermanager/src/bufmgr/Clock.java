package bufmgr;



/**
 * The "Clock" replacement policy.
 */
class Clock extends Replacer {

  
  protected static final int AVAILABLE = 10;
  protected static final int REFERENCED = 11;
  protected static final int PINNED = 12;
  
  
  
  /** Clock head; required for the default clock algorithm. */
  protected int head;
  int target;
  int numbufs;
 

  // --------------------------------------------------------------------------

  /**
   * Constructs a clock replacer.
   */
   public Clock(BufMgr bufmgr) {
    super(bufmgr);
     numbufs = bufmgr.getNumBuffers();
   
    for (int i = 0; i < frametab.length; i++) {
      frametab[i].state = AVAILABLE;
    }

    
    head = -1;
    target = -1;
    
  } 
   

  /**
   * Notifies the replacer of a new page.
   */
  
  public void newPage(FrameDesc fdesc) {
    // no need to update frame state
  }

  /**
   * Notifies the replacer of a free page.
   */
  public void freePage(FrameDesc fdesc) {
    fdesc.state = AVAILABLE;
    
  }

  /**
   * Notifies the replacer of a pined page.
   */
  public void pinPage(FrameDesc fdesc) {
	  fdesc.state = PINNED;
	 
  }

  /**
   * Notifies the replacer of an unpinned page.
   */
  public void unpinPage(FrameDesc fdesc) {
	  if(fdesc.pincnt==0){
		 
		  fdesc.state=REFERENCED;
	  }
  }

  /**
   * Selects the best frame to use for pinning a new page.
   * 
   * @return victim frame number, or -1 if none available
   */
  public int pickVictim() {


	  int loop_limt=0;
	  int counter=0; //intializing counter to restrict iteration
	  
	  do
	  {
		  head = (head+1) % numbufs;
		  
		  if(loop_limt > 2 * numbufs){
			  return -1;
		  }
		  if(frametab[head].state == REFERENCED){
			  frametab[head].state = AVAILABLE;
		  }
		  if(frametab[head].state == AVAILABLE){
			  counter=1;
		  }
			 
		  loop_limt++;
	  }while(counter==0);
	  	  return head;
	 
			
	  
  }

} 
