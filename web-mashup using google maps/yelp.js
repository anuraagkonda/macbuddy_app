var geocoder;
var map;
var ne;
var sw;
var swlat;
var swlng;
var nelat;
var nelng;
var markersArr = [];
function initialize () {
   
  var mapOptions = {
    zoom: 16,
    center: new google.maps.LatLng(32.75, -97.13)
  };
      geocoder = new google.maps.Geocoder();
      map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);
      google.maps.event.addListener(map, 'tilesloaded', function(){
      var initialBounds;
      initialBounds = map.getBounds(); 
                 //alert(map.getBounds());
                 var ne = initialBounds.getNorthEast();
                 var sw = initialBounds.getSouthWest();
                 nelat = ne.lat();
                 nelng = ne.lng();
                 swlat = sw.lat();
                 swlng = sw.lng();
      });  
      google.maps.event.addListener(map, 'dragend', function(){
      var initialBounds;
      initialBounds = map.getBounds(); 
                 //alert(map.getBounds());
                 var ne = initialBounds.getNorthEast();
                 var sw = initialBounds.getSouthWest();
                 nelat = ne.lat();
                 nelng = ne.lng();
                 swlat = sw.lat();
                 swlng = sw.lng();
      });  

   }


function codeAddress(x,p) {
    var address=x.toString();
    if(p==8){

      p = 16;
    }
    if(p==9){

      p = 17;
    }
    //geocoder.setBounds(swlat, swlng, nelat, nelng);
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {

        map.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
            map: map,
            position: results[0].geometry.location,
            icon: "http://maps.google.com/mapfiles/kml/pal3/icon" + p + ".png"
            
        });
      } else {
        alert("Geocode was not successful for the following reason: " + status);
      }
      markersArr.push(marker);
    });
    
  }

function clearmapmarkers(){
   
   //alert(markersArr.length);
   for(var i=0; i<markersArr.length; i++){

       markersArr[i].setMap(null);
   }
   
   markersArr.length = 0;
}

function sendRequest1 () {
  
   var xhr = new XMLHttpRequest();
   var query = encodeURI(document.getElementById("search").value);
   xhr.open("GET", "proxy.php?term="+query+"&bounds="+swlat+","+swlng+"|"+nelat+","+nelng+"&limit=10");
   xhr.setRequestHeader("Accept","application/json");
   xhr.onreadystatechange = function () {
       if (this.readyState == 4) {
          var json = JSON.parse(this.responseText);
          var str = JSON.stringify(json,undefined,2);
          //alert("lengthka");
          //alert(json.businesses.length);
          //alert(str);
          clearmapmarkers();
          var out = "<table border=2>";
          for(i=0; i<json.businesses.length; i++){
          x = i+1;
          var j = json.businesses[i];
          out += "<tr><td>" + x +
                  "</td><td>"+
                  "<a href = "+j.url+">"+j.name+"</a>"+//jason response in html table.
                  "</td><td>" +"<img src="+j.image_url+">"+
                   "</td><td>"+"<img src="+ j.rating_img_url+">"+
                   "</td><td>"+
                  j.snippet_text+    
                  "</td></tr>";
          //alert(json.businesses[i].location.display_address[0]+","+json.businesses[i].location.display_address[2]);  j.snippet_text
          codeAddress(j.location.address+","+j.location.postal_code+","+j.location.state_code+","+j.location.country_code,i);
          }
          out += "</table>";
          document.getElementById("output").innerHTML = "<pre>" + out + "</pre>";
       }
   };
   xhr.send(null);

}

