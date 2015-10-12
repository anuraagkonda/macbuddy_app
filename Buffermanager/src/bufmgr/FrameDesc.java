package bufmgr;

import global.PageId;

/**
 * A frame descriptor; contains info about each page in the buffer pool.
 */
class FrameDesc {

  /** Index in the buffer pool. */
  public int index;
  
  /** Identifies the frame's page. */
  public PageId pageno;

  /** The frame's pin count. */
  public int pincnt;

  /** The frame's dirty status. */
  public boolean dirty;

  /** Generic state used by replacers. */
  public int state;

  // --------------------------------------------------------------------------

  /**
   * Default constructor; empty frame.
   */
  public FrameDesc(int index) {
    this.index = index;
    pageno = new PageId();
    pincnt = 0;
    dirty = false;
    state = 0;
  }
  	public int get_pincount(){
	   return this.pincnt;
  	}
  	public int get_pageid(){
	  return this.pageno.pid;
  	}
  	public boolean get_dirty(){
	   return this.dirty;
  	}
  	public int get_state(){
	  return state;
  	}
  	public void set_pincount(int n){
  		this.pincnt = n;
  	}
  	public void set_dirty(){
  		this.dirty=true;
  	}
  }
