package bufmgr;

import global.GlobalConst;
import global.Page;
import global.PageId;
import global.Minibase;
import java.util.HashMap;
//import java.util.*;
//import java.io.*;

/**
 * <h3>Minibase Buffer Manager</h3>
 * The buffer manager reads disk pages into a main memory page as needed. The
 * collection of main memory pages (called frames) used by the buffer manager
 * for this purpose is called the buffer pool. This is just an array of Page
 * objects. The buffer manager is used by access methods, heap files, and
 * relational operators to read, write, allocate, and de-allocate pages.
 */
public class BufMgr  implements GlobalConst  {

    /** Actual pool of pages (can be viewed as an array of byte arrays). */
    protected Page[] bufpool;

    /** Array of descriptors, each containing the pin count, dirty status, etc\
	. */
    protected  FrameDesc[] frametab;

    /** Maps current page numbers to frames; used for efficient lookups. */
    protected HashMap<Integer, FrameDesc> pagemap;

    /** The replacement policy to use. */
    protected Replacer replacer;
    
   
     int list_num=0;


  /**
   * Constructs a buffer mamanger with the given settings.
   * 
   * @param numbufs number of buffers in the buffer pool
   */
  public BufMgr(int numbufs) {
    
	list_num = numbufs;
	bufpool = new Page[numbufs];
	frametab = new FrameDesc[numbufs];
	for(int i=0; i<numbufs; i++){
		frametab[i]= new FrameDesc(i);
		bufpool[i] = new Page();
	}
	
	pagemap = new HashMap<Integer, FrameDesc>(numbufs);
	replacer = new Clock(this); // intializing replacer with current object
	
	
	
  }
  
  /**
   * Allocates a set of new pages, and pins the first one in an appropriate
   * frame in the buffer pool.
   * 
   * @param firstpg holds the contents of the first page
   * @param run_size number of pages to allocate
   * @return page id of the first new page
   * @throws IllegalArgumentException if PIN_MEMCPY and the page is pinned
   * @throws IllegalStateException if all pages are pinned (i.e. pool exceeded)
   */
  public PageId newPage(Page firstpg, int run_size){
	  PageId pgid = Minibase.DiskManager.allocate_page(run_size);
	  
	   try{
	   pinPage(pgid, firstpg, true); 
	   }
	   catch(Exception e){
		   for(int i = 0 ; i < run_size; i++ )
		   {
			  pgid.pid += i;
		   Minibase.DiskManager.deallocate_page(pgid); //deallocating all invalid pages
		   
		   }
		   return null;
		   
	   }
	   replacer.newPage(pagemap.get(pgid.pid));
	  
	  
	  return pgid;
}

  /**
   * Deallocates a single page from disk, freeing it from the pool if needed.
   * 
   * @param pageno identifies the page to remove
   * @throws IllegalArgumentException if the page is pinned
   */
  public void freePage(PageId pageno) {
		FrameDesc findex = pagemap.get(pageno.pid);
		if(findex != null){
			
		    if(findex.get_pincount()>0){
			throw new IllegalArgumentException("page is pinned");
		    }
		    else
		    {
			
				
				pagemap.remove(pageno.pid);
				findex.pageno.pid = INVALID_PAGEID;
				findex.pincnt=0;
				findex.dirty=false;
				replacer.freePage(findex);
		    }
		Minibase.DiskManager.deallocate_page(pageno);
		}
  
  }

  /**
   * Pins a disk page into the buffer pool. If the page is already pinned, this
   * simply increments the pin count. Otherwise, this selects another page in
   * the pool to replace, flushing it to disk if dirty.
   * 
   * @param pageno identifies the page to pin
   * @param page holds contents of the page, either an input or output param
   * @param skipRead PIN_MEMCPY (replace in pool); PIN_DISKIO (read the page in)
   * @throws IllegalArgumentException if PIN_MEMCPY and the page is pinned
   * @throws IllegalStateException if all pages are pinned (i.e. pool exceeded)
   */
  public void pinPage(PageId pageno, Page page, boolean skipRead) {
	int head=0;
	FrameDesc findex = pagemap.get(pageno.pid);
	
	if(findex != null)
	{
		if(skipRead)
	{
		
		throw new IllegalArgumentException("invalid argument");
	}
	else
	{
		findex.set_pincount(findex.get_pincount()+1);
		replacer.pinPage(findex);
		page.setPage(bufpool[findex.index]);
		return;
	}
	}
	else
	{
		    
		    head = replacer.pickVictim();
		    if(head<0){
		    	throw new IllegalStateException("buffer is full");
		    }
		    findex = frametab[head];
		    
			if(findex.pageno.pid != -1){
				
				pagemap.remove(findex.pageno.pid);
				if(findex.get_dirty())
				Minibase.DiskManager.write_page(findex.pageno, bufpool[head]);
			}
				
				if(skipRead)
				{
					bufpool[head].copyPage(page);
				}
				else{
				
					Minibase.DiskManager.read_page(pageno,bufpool[head]);
				}
				page.setPage(bufpool[head]);// setting up page data pointers
				findex.pageno.pid = pageno.pid;
				findex.pincnt=1;
				findex.dirty=false;
				pagemap.put(pageno.pid, findex); //mapping pageid with frame index
				
				replacer.pinPage(findex);
	}
	}
	
		
	
		
	
    //throw new UnsupportedOperationException("Not implemented");

  /**
   * Unpins a disk page from the buffer pool, decreasing its pin count.
   * 
   * @param pageno identifies the page to unpin
   * @param dirty UNPIN_DIRTY if the page was modified, UNPIN_CLEAN otherrwise
   * @throws IllegalArgumentException if the page is not present or not pinned
   */
  public void unpinPage(PageId pageno, boolean dirty) {
    
	  FrameDesc findex;
	 
	   findex = pagemap.get(pageno.pid);
	 
	  if(findex == null)
		  throw new IllegalArgumentException("Page not here");
	  else{
	             if( findex.get_pincount()>0){
		  	     
	            	 findex.set_pincount(findex.get_pincount()-1);
		  		 findex.dirty=dirty;
		  		 replacer.unpinPage(findex);
		  		 return;
	             }
	  }
  }

  /**
   * Immediately writes a page in the buffer pool to disk, if dirty.
   */
  public void flushPage(PageId pageno) {
    
	  for(int i =0; i<frametab.length ; i ++)
	  {
	  if((pageno == null || frametab[i].pageno.pid == pageno.pid ) && frametab[i].dirty) {
		  Minibase.DiskManager.write_page(frametab[i].pageno, bufpool[i]);
		  frametab[i].dirty = false;
	  }
	  }
  }

  /**
   * Immediately writes all dirty pages in the buffer pool to disk.
   */
  public void flushAllPages() {
	  for(int i=0; i<frametab.length; i++)
	  {
		  
			  flushPage(frametab[i].pageno);
		  
	  }
    
  }

  /**
   * Gets the total number of buffer frames.
   */
  public int getNumBuffers() {
	  
	  return bufpool.length;
  
  }

  /**
   * Gets the total number of unpinned buffer frames.
   */
  public int getNumUnpinned() {
	 
	  int cnt=0;
	  for(int i=0;i<bufpool.length;i++){
		  if(frametab[i].get_pincount()==0){
  			  cnt = cnt+1;
		  }
		  System.out.println("index"+" "+frametab[i].index+" "+frametab[i].get_pincount());
			  
	  }
	  return cnt;
    
  }
 



}
