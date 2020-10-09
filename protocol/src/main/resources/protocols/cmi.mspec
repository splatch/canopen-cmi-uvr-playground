/*
 * Page entry is constructed from two addresses, 24 bits each and two extra bytes (separator/marker),
 * which turns into 64 bits, termination of array is sequence 32 bit 0x0.
 * have more than 32 bits/4 bytes
 */

[type 'DisplayPages'
  [reserved uint  8 '0x60']
  [reserved uint 24 '0x000000']
  [reserved uint  8 '0x00']
  [reserved uint  8 '0x01']
  [reserved uint 16 '0x0000']
  [reserved uint 32 '0x00000000']
  [reserved uint  8 '0x9a']
  [reserved uint  8 '0x61']
  [reserved uint 16 '0x0000']
  [reserved uint  8 '0xba']
  [reserved uint  8 '0xb8']
  [reserved uint  8 '0xa0']
  [reserved uint  8 '0x5c']
  [reserved uint 32 '0x00000000']
  [array DisplayPage 'pages' terminated 'STATIC_CALL("brute.force.proto.Util.isLastElement", io)']
  [reserved uint 32 '0x00000000']
]

[type 'DisplayPage'
  [simple uint 8 'separator']
  [simple IndexAddress 'text']
  [simple uint 8 'marker']
  [simple IndexAddress 'link']
]

[type 'IndexAddress'
  [simple uint  8 'subIndex']
  [simple uint 16 'index'   ]
]

[type 'TextBlock' [int 32 'size']
    [reserved uint 8 '0x1f']

]
