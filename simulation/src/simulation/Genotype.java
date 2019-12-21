package simulation;
import java.util.Random;

public class Genotype {
    int[] genes;
    int dominating;

    //search for dominating gene
    private void createDominatingGene(int[] genes){
        int max=0;
        int maxIdx=0;
        for(int i=0;i<8;i++)
            if(genes[i]>max){
                max=genes[i];
                maxIdx=i;
            }
        this.dominating=maxIdx;
    }
    //write genotype to repeating array
    public int[] toArray(){

        int[] array=new int[32];
        int idx=0;
        for(int i=0;i<8;i++)
            for(int y=0;y<genes[i];y++){
                array[idx]=i;
                idx++;
            }
        return array;
    }
    //create random genotype
    public Genotype(){
        genes=new int[8];
        Random gen=new Random();
        for(int i=0;i<32;i++)
            genes[gen.nextInt(8)]++;

        createDominatingGene(this.genes);
    }
    //create genotype from parents
    public Genotype(Animal parent1,Animal parent2){
        Genotype gene1=parent1.genes;
        Genotype gene2=parent2.genes;

        genes=new int[8];

        Random gen=new Random();
        //split in random places to three groupes
        int[] split=new int[4];
        split[0]=0;
        split[3]=31;
        while(split[1]==split[2]){
            split[1]=gen.nextInt(30)+1;
            split[2]=gen.nextInt(30)+1;
        }
        if(split[2]<split[1]){
            int temp=split[0];
            split[0]=split[1];
            split[1]=temp;
        }

        //pick random group
        int randomSplit=gen.nextInt(3);
        int randomSplit2=gen.nextInt(3);
        while(randomSplit==randomSplit2)
            randomSplit2=gen.nextInt(3);


        int[] array1=gene1.toArray();
        int[] array2=gene2.toArray();
        //first and second group
        int sum=0;

        for(int i=split[randomSplit];i<split[randomSplit+1];i++){
            genes[array1[i]]++;
            sum++;
        }

        for(int i=split[randomSplit2];i<split[randomSplit2+1];i++){
            genes[array1[i]]++;
            sum++;
        }

        //third group
        while(split[1]==split[2]){
            split[1]=gen.nextInt(30)+1;
            split[2]=gen.nextInt(30)+1;
        }
        int randomSplit3=gen.nextInt(3);
        for(int i=split[randomSplit3];i<split[randomSplit3+1];i++){
            if(sum==32){
                break;}
            genes[array2[i]]++;
            sum++;
        }

        //random genes
        while(sum<32){
            genes[gen.nextInt(8)]++;
            sum++;
        }

        //fill zeroes
        for(int i=0;i<8;i++){
            if(genes[i]==0){
                for(int y=0;y<8;y++)
                    if(genes[y]>=2){
                        genes[y]--;
                        genes[i]++;
                        break;
                    }
            }
        }


     createDominatingGene(this.genes);
    }


}
