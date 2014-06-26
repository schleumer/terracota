rm ./main.bin
g++ -O3 -v main.cpp -o main.bin
echo
echo "==================== STARTING ===================="
echo
chmod 777 main.bin
./main.bin
