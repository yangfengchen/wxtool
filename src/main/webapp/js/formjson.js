function fromJson(dataArray){
    var _json = {};
    $.each(dataArray,function(i,v){
        _json[v.name] = v.value;
    });
    return _json;
}