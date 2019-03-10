(function() {

  "use strict";
  /**
   * Function to check if we clicked inside an element with a particular class
   * name.
   */
  function clickInsideElement( e, className ) {
    var el = e.srcElement || e.target;

    if ( el.classList.contains(className) ) {
      return el;
    } else {
      while ( el = el.parentNode ) {
        if ( el.classList && el.classList.contains(className) ) {
          return el;
        }
      }
    }

    return false;
  }

  /**
   * Get's exact position of event.
   */
  function getPosition(e) {
    var posx = 0;
    var posy = 0;

    if (!e) var e = window.event;

    if (e.pageX || e.pageY) {
      posx = e.pageX;
      posy = e.pageY;
    } else if (e.clientX || e.clientY) {
      posx = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
      posy = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
    }

    return {
      x: posx,
      y: posy
    }
  }

  /**
   * Variables.
   */
  var contextMenuClassName = "context-menu";
  var contextMenuItemClassName = "context-menu__item";
  var contextMenuLinkClassName = "context-menu__link";
  var contextMenuActive = "context-menu--active";

  var itemClassName = "item";
  var itemInContext;

  var clickCoords;
  var clickCoordsX;
  var clickCoordsY;

  var menu = document.querySelector("#context-menu");
  var menuItems = menu.querySelectorAll(".context-menu__item");
  var menuState = 0;
  var menuWidth;
  var menuHeight;
  var menuPosition;
  var menuPositionX;
  var menuPositionY;

  var windowWidth;
  var windowHeight;


  function init() {
    contextListener();
    clickListener();
    keyupListener();
    resizeListener();
  }


  function contextListener() {
    document.addEventListener( "contextmenu", function(e) {
      itemInContext = clickInsideElement( e, itemClassName );

      if ( itemInContext ) {
        e.preventDefault();
        toggleMenuOn();
        positionMenu(e);
      } else {
        itemInContext = null;
        toggleMenuOff();
      }
    });
  }


  function clickListener() {
    document.addEventListener( "click", function(e) {
      var clickeElIsLink = clickInsideElement( e, contextMenuLinkClassName );

      if ( clickeElIsLink ) {
        e.preventDefault();
        menuItemListener( clickeElIsLink );
      } else {
        var button = e.which || e.button;
        if ( button === 1 ) {
          toggleMenuOff();
        }
      }
    });
  }


  function keyupListener() {
    window.onkeyup = function(e) {
      if ( e.keyCode === 27 ) {
        toggleMenuOff();
      }
    }
  }


  function resizeListener() {
    window.onresize = function(e) {
      toggleMenuOff();
    };
  }


  function toggleMenuOn() {
    if ( menuState !== 1 ) {
      menuState = 1;
      menu.classList.add( contextMenuActive );
    }
  }


  function toggleMenuOff() {
    if ( menuState !== 0 ) {
      menuState = 0;
      menu.classList.remove( contextMenuActive );
    }
  }


  function positionMenu(e) {
    clickCoords = getPosition(e);
    clickCoordsX = clickCoords.x;
    clickCoordsY = clickCoords.y;

    menuWidth = menu.offsetWidth + 4;
    menuHeight = menu.offsetHeight + 4;

    windowWidth = window.innerWidth;
    windowHeight = window.innerHeight;

    if ( (windowWidth - clickCoordsX) < menuWidth ) {
      menu.style.left = windowWidth - menuWidth + "px";
    } else {
      menu.style.left = clickCoordsX + "px";
    }

    if ( (windowHeight - clickCoordsY) < menuHeight ) {
      menu.style.top = windowHeight - menuHeight + "px";
    } else {
      menu.style.top = clickCoordsY + "px";
    }
  }


  function menuItemListener( link ) {
    console.log("Context Menu clicked");
    console.log( "Task ID - " + itemInContext.querySelector(".label").innerHTML + ", Task action - " + link.getAttribute("data-action"));
    var filename = itemInContext.querySelector(".label").innerHTML;
    toggleMenuOff();
    if (link.getAttribute("data-action") == "Rename"){
      modal = document.getElementById('renameModal');
      modal.style.display = "block";
      var newName = document.getElementById("renameFile").value;
      var filePath = currentPath+"/"+filename;
      document.getElementById("okRenameFile").addEventListener("click", renameDeleteRequest(filePath, "Rename", newName));
    }
    if (link.getAttribute("data-action") == "Permissions"){
      modal = document.getElementById('permissionsModal');
      modal.style.display = "block";
    }

    if (link.getAttribute("data-action") == "View"){
      console.log(currentPath+"/"+filename);
      window.location.replace("/viewFile?file="+currentPath+"/"+filename+"#"+currentPath);
    }

    if (link.getAttribute("data-action") == "Edit"){
      window.location.replace("/editFile?file="+currentPath+"/"+filename+"#"+currentPath);
    }

    if (link.getAttribute("data-action") == "Delete"){
      var filePath = currentPath+"/"+filename;
      renameDeleteRequest(filename, "Delete", "");
    }

  }

  function renameDeleteRequest(path, request, newName){
    return function(){
      var xhttp;
      xhttp=new XMLHttpRequest();
      xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
          handleResponse(this);
        }
      };
      var url;
      if(request=="Rename"){
        url = "/"+request+"?file="+path+"?newName="+newName;
      }
      else{
        url = "/"+request+"?file="+path;
      }
      xhttp.open("GET", url, false);
      xhttp.send();
      modal.style.display = "none";
    }
  }

  function handleResponse(xhttp){
    var response = xhttp.responseText;
    if(response=="Denied"){
      //notify user access denied
      return;
    }
    //get info
    var url = "/getInodes.t?path="+currentPath;
    getInfo(url, updateContent);
  }

  /**
   * Run the app.
   */
  init();

})();
