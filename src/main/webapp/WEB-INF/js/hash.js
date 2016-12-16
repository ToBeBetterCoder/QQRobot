var hash2 = function(uin,ptvfwebqq){
	uin += "";
	var ptb = [];
	for (var i=0;i<ptvfwebqq.length;i++){
		var ptbIndex = i%4;
		ptb[ptbIndex] ^= ptvfwebqq.charCodeAt(i);
	}
	var salt = ["EC", "OK"];
	var uinByte = [];
	uinByte[0] = (((uin >> 24) & 0xFF) ^ salt[0].charCodeAt(0));
	uinByte[1] = (((uin >> 16) & 0xFF) ^ salt[0].charCodeAt(1));
	uinByte[2] = (((uin >> 8) & 0xFF) ^ salt[1].charCodeAt(0));
	uinByte[3] = ((uin & 0xFF) ^ salt[1].charCodeAt(1));
	var result = [];
	for (var i=0;i<8;i++){
		if (i%2 == 0)
			result[i] = ptb[i>>1];
		else
			result[i] = uinByte[i>>1];
	}
	return byte2hex(result);

};


var byte2hex = function(bytes){//bytes array
	var hex = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'];
	var buf = "";

	for (var i=0;i<bytes.length;i++){
		buf += (hex[(bytes[i]>>4) & 0xF]);
		buf += (hex[bytes[i] & 0xF]);
	}
	return buf;
}