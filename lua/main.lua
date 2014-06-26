require 'struct'
local pl = require 'pl.pretty'

local file = io.open("daworld4.wld", "rb")

local bytes = file:read(8)

s = struct.unpack("<i", bytes)

grid = {}
for i = 0, 3 do
    grid[i] = {}

    for j = 1, 5 do
        grid[i][j] = 0 -- Fill the values here
    end
end

pl.dump(grid)