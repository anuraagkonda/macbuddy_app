<?php  

   session_start();
   $_SESSION['id'] = session_id();
   

?>
<html>
<head><title>Buy Products</title></head>
<body>
<!-- Reference http://developer.ebay.com/DevZone/finding/HowTo/GettingStarted_PHP_NV_XML/GettingStarted_PHP_NV_XML.html -->
<?php
//error_reporting(E_ALL);
ini_set('display_errors','Off');
//$xmlquery = 'http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&trackingId=7000610&category=9007&keyword=dell+i5&numItems=5';
$xmlquery = 'http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/CategoryTree?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&visitorUserAgent&visitorIPAddress&trackingId=7000610&categoryId=72&showAllDescendants=true';
$xmlstr = file_get_contents($xmlquery);
$xml = new SimpleXMLElement($xmlstr);
//header('Content-Type: text/xml');
?>



<div id="session_basket">
<?php
$addid;
$removeid;
$counter=0;
if(isset($_SESSION['id'])){
	if(!isset($_SESSION['product_id'])){

		$_SESSION['product_id'] = array();

	}
}


    
	
	if(isset($_SESSION['id'])){
		
		$c = sizeof($_SESSION['product_id']);
		for($i=0; $i<$c; $i++){
			  	if($_SESSION['product_id'][$i]==$_GET["buy"]){

			  		$counter=$counter + 1;
			  	}
			  }
        if($counter==0) {	
        if($_GET["buy"]!=null){	  
			$addid = $_GET["buy"];
			array_push($_SESSION['product_id'], $addid);
			}
		}
		
	}

		if($_GET["clear"]==1){

			foreach($_SESSION['product_id'] as $i => $value){

				unset($_SESSION['product_id'][$i]);
			}
			$_SESSION['total']=0;
		}


		 	if($_GET["delete"]!=null){
		 	  $c = sizeof($_SESSION['product_id']);
		 	  for($i=0; $i<$c; $i++){
		 		if($_SESSION['product_id'][$i]==$_GET["delete"])
		 		{
		 			
		 			$_SESSION['product_id'][$i]=null;

		 		}

			  }

			 
			  
			   	$xmlprod=file_get_contents('http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&visitorUserAgent&visitorIPAddress&trackingId=7000610&productId='.$_GET["delete"]);
     	 		$xmlobj = new SimpleXMLElement($xmlprod);
     	 		$p = $xmlobj->categories->category->items->product->minPrice;
			  	$_SESSION['total']=$_SESSION['total']-$p;
			  
			}
     
    
     ?>
     <table border="2"> 
     <?php
     $c = sizeof($_SESSION['product_id']);
     //print_r($_SESSION['product_id']);
     for($i=0; $i<$c; $i++){
     	if($_SESSION['product_id'][$i]!=null){

     		
     	 $xmlprod=file_get_contents('http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&visitorUserAgent&visitorIPAddress&trackingId=7000610&productId='.$_SESSION['product_id'][$i]);
     	 $xmlobj = new SimpleXMLElement($xmlprod);
     	 $p = $xmlobj->categories->category->items->product;
     	 ?>
     	<tr>
		<td><?php print $p['id'] ?></td>
		<td><?php print $p->name; ?></td>
	    <td><img src="<?php print $p->images->image->sourceURL; ?>"> </td>
	    <td><?php print $p->minPrice; ?></td>
	    <td><?php print $p->fullDescription; ?></td>
	    <td><a href="<?php print $p->productOffersURL; ?>">productoffer</a></td>
	    <td><a href="<?php print "buy.php?delete=".$p['id']; ?>">delete</a></td>
	   </tr>

     <?php
 		}
    
      }
    ?>
      </table>
      <p>
      <table>
      	<tr>
	      <td>
	      <?php 
	      if($_GET["buy"]!=null && $counter==0){
	      	$xmlprod=file_get_contents('http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&visitorUserAgent&visitorIPAddress&trackingId=7000610&productId='.$_GET["buy"]);
     	 	$xmlobj = new SimpleXMLElement($xmlprod);
     	 	$p = $xmlobj->categories->category->items->product;
	      	print " Total Price: ".$_SESSION['total']+=$p->minPrice;	
	  		}
	  		else{

	  			print "<b> Total Price: </b>".$_SESSION['total'];
	  		}
	      ?>
	      </td>
	   </tr>
      </table>
      </p>
     <!--<div id="empty">
     <a href="buy.php?clear=1">Empty</a>
     </div>-->
     <div>
     <form action="buy.php" method="GET">
		<input name="clear" value="1" type="hidden">
		<input value="Empty Basket" type="submit">
	</form>
	</div>

   
</div>
<div id="category">
<form method="GET" action="buy.php">
 Select 
<select name = "selected_part" >
<option value = "<?php print $xml->category['id']; ?>" ><?php print $xml->category->name; ?></option>
<?php
  foreach($xml->category->categories->category as $cat){ ?>
  
  <optgroup value="<?php print $cat['id']; ?>" label="<?php print $cat->name; ?>">
  <option value="<?php print $cat['id']; ?>"><?php print $cat->name; ?></option>
  
  <?php foreach($cat->categories->category as $cat2){ ?>
   
   <option value="<?php print $cat2['id']; ?>" value = "<?php print $cat2->name; ?>"><?php print $cat2->name; ?></option>
  
  <?php } ?>
  
  </optgroup>
  
<?php } ?>

</select>

&nbsp; &nbsp; Keyword<input type="text" name = "keyword"></input></br>
</br>
<input type="submit" name = "submit"></input>
</form>
</div>   
<div id="result">
	<?php
		$keyword;
		$xmlobj;
		if($_GET["keyword"]==null){
			$query='http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&visitorUserAgent&visitorIPAddress&trackingId=7000610&categoryId='.$_GET["selected_part"].'&numItems=20';
			}
		else{
		    $query = 'http://sandbox.api.ebaycommercenetwork.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&trackingId=7000610&category='.$_GET["selected_part"].'&keyword='.$_GET["keyword"].'&numItems=20';
			} 
		$send = str_replace(" ","%20",$query);  //to the space in keyword with %20
		$xmllist = file_get_contents($send);
		$xmlobj = new SimpleXMLElement($xmllist);

		if(!isset($_GET['submit']) || $_GET["buy"]!=null){   //to store search results in session

			$xmllist = file_get_contents($_SESSION['qry']);
			$xmlobj = new SimpleXMLElement($xmllist);

		}
		else
		{	try{
			$xmllist = file_get_contents($send);
			$xmlobj = new SimpleXMLElement($xmllist);
			$_SESSION['qry']=$send;
			}
			catch(Exception $e){

				$_SESSION['qry']=null;
			}
			
		}
        
	  ?>


	<table border="2">
	<?php foreach($xmlobj->categories->category->items->product as $p){?>
	<tr>
		<td><?php print $p['id'] ?></td>
	    <td><a href="<?php print "buy.php?buy=".$p['id']; ?>"><?php print $p->name; ?></a></td>
	    <td><img src="<?php print $p->images->image->sourceURL; ?>" /> </td>
	    <td><?php print $p->minPrice; ?></td>
	    <td><?php print $p->fullDescription; ?></td>
	    <td><a href="<?php print $p->productOffersURL; ?>">productoffer</a></td>
	</tr>
	<?php } 

	?>
	</table>
	</div>
  </body>
</html>
