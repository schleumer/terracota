rm ./main.bin
g++ -v main.cpp -o main.bin 
echo
echo "==================== STARTING ===================="
echo
chmod 777 main.bin
./main.bin
