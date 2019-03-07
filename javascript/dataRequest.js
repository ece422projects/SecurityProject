var currentPath = "userHome/";

function getInfo(url, cFunction){
    var xhttp;
    xhttp=new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        cFunction(this);
      }
    };
    xhttp.open("GET", url, true);
    xhttp.send();
}

function updateInfo(){
  //do UI stuff
  console.log("updateInfo");
  var url = "getInodes.t?path="+currentPath;
  getInfo(url, updateContent);
}

function updateContent(xhttp){
  console.log("Response: " + xhttp.responseText);
  var content_container = document.getElementsByClassName("content_container")[0];
  var inodes = JSON.parse(xhttp.responseText);

  var itemDiv;
  var itemSpan1;
  var itemIcon;
  var itemSpan2;

  var i;
  for(i=0; i<inodes.length; i++){
    itemDiv = document.createElement('div');
    itemSpan1 = document.createElement('span');
    itemIcon = document.createElement('i');
    itemSpan2 = document.createElement('span');

    itemDiv.className = "item";
    itemSpan2.className = "label";
    itemSpan2.appendChild(document.createTextNode(inodes[i].name));

    if(inodes[i].type=="file"){
      itemSpan1.className = "file";
      itemIcon.className = "fas fa-file";

      itemSpan1.appendChild(itemIcon);
      itemDiv.appendChild(itemSpan1);
      itemDiv.appendChild(itemSpan2);
    }

    if(inodes[i].type=="folder"){
      itemSpan1.className = "folder";
      itemIcon.className = "fas fa-folder";

      itemSpan1.appendChild(itemIcon);
      itemDiv.appendChild(itemSpan1);
      itemDiv.appendChild(itemSpan2);
    }
    content_container.appendChild(itemDiv);
  }
}
